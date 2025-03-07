package jungmo.server.domain.service;

import jungmo.server.domain.dto.request.PasswordRequest;
import jungmo.server.domain.dto.request.UserCodeRequest;
import jungmo.server.domain.dto.request.UserRequest;
import jungmo.server.domain.dto.response.UserInfoResponse;
import jungmo.server.domain.dto.response.UserResponse;
import jungmo.server.domain.entity.User;
import jungmo.server.domain.provider.UserDataProvider;
import jungmo.server.domain.repository.UserRepository;
import jungmo.server.global.auth.dto.request.RegisterRequestDto;
import jungmo.server.global.auth.dto.response.SecurityUserDto;
import jungmo.server.global.auth.service.AuthService;
import jungmo.server.global.auth.service.KakaoService;
import jungmo.server.global.auth.service.PrincipalDetails;
import jungmo.server.global.auth.service.S3Service;
import jungmo.server.global.error.ErrorCode;
import jungmo.server.global.error.exception.BusinessException;
import jungmo.server.global.util.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final KakaoService kakaoService;
    private final JwtTokenProvider tokenProvider;
    private final PasswordEncoder passwordEncoder;
    private final S3Service s3Service;
    private final UserDataProvider userDataProvider;
    private final Random random = new Random();
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    /**
     * 유저코드로 유저를 찾는 로직
     * @param userCode
     * @return
     */
    @Transactional(readOnly = true)
    public List<UserResponse> findUser(String userCode) {
        return userDataProvider.findUserByUserCode(userCode).stream()
                .map(User::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public UserResponse findUserById(Long userId) {
       return userDataProvider.findUserById(userId).toDto();
    }

    /**
     * 유저 수정 메서드
     * @param userDto
     * @return
     * @throws IOException
     */
    @Transactional
    public Long updateUserProfile(UserRequest userDto) throws IOException {
        User user = userDataProvider.getUser();
        try {
            if (userDto.getProfileImage() != null && !userDto.getProfileImage().isEmpty()) {
                String profileImageUrl = s3Service.uploadFile(userDto.getProfileImage(), user.getId());
                user.setProfileImage(profileImageUrl);
            } else if (Boolean.TRUE.equals(userDto.getDelete())){
                user.setProfileImage(null); // 이미지 삭제 요청
            }

        } catch (Exception e) {
            throw new BusinessException(ErrorCode.IMAGE_UPLOAD_FAIL);
        }
        // 닉네임 수정
        user.setUserName(userDto.getUserName());
        return user.getId();
    }

    /**
     * 내 정보 조회하는 로직
     * @return
     */
    @Transactional(readOnly = true)
    public UserInfoResponse getUserInfo() {
        User user = userDataProvider.getUser();
        return new UserInfoResponse(user.getId(), user.getUserCode(), user.getUserName(), user.getProfileImage(), user.getProvider());
    }


    @Transactional
    public User createUser(RegisterRequestDto request, String encodedPassword) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }

        // 고유 코드 생성
        String uniqueCode = generateUniqueUserCode();

        User user = User.builder()
                .email(request.getEmail())
                .password(encodedPassword)
                .userName(request.getName())
                .provider("email")
                .role("ROLE_USER")
                .userCode(uniqueCode) // 고유 코드 설정
                .build();

        return userRepository.save(user);
    }

    @Transactional
    public void changePassword(PasswordRequest request) {
        User user = userDataProvider.getUser();

        if (Objects.equals(user.getProvider(), "kakao")) {
            throw new BusinessException(ErrorCode.UNABLE_TO_UPDATE_PASSWORD);
        }

        // 현재 비밀번호 검증
        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new BusinessException(ErrorCode.PASSWORD_NOT_MATCH);
        }

        // 새로운 비밀번호와 기존 비밀번호 비교
        if (passwordEncoder.matches(request.getNewPassword(), user.getPassword())) {
            throw new BusinessException(ErrorCode.INVALID_PASSWORD);
        }

        // 새로운 비밀번호 암호화 및 저장
        user.updatePassword(passwordEncoder.encode(request.getNewPassword()));

    }

    @Transactional
    public void deleteUser(String accessToken, String refreshToken) {
        User user = userDataProvider.getUser();
        tokenProvider.invalidateTokens(accessToken, refreshToken);

        if ("kakao".equals(user.getProvider())) {
            kakaoService.unlinkKakaoAccount(user.getOauthId());
        }
        user.deactivate();
    }

    @Transactional
    public void logout(String accessToken, String refreshToken) {
        tokenProvider.invalidateTokens(accessToken,refreshToken);
    }

    // 고유 코드 생성 (6자리 랜덤 문자열)
    public String generateUniqueUserCode() {
        String code;
        do {
            code = generateRandomString(6);
        } while (userRepository.existsByUserCode(code)); // 중복 검사
        return code;
    }

    // 랜덤 문자열 생성
    private String generateRandomString(int length) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(CHARACTERS.length());
            sb.append(CHARACTERS.charAt(index));
        }
        return sb.toString();
    }

}
