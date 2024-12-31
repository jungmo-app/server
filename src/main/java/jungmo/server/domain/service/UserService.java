package jungmo.server.domain.service;

import jungmo.server.domain.dto.request.UserCodeDto;
import jungmo.server.domain.dto.request.UserRequestDto;
import jungmo.server.domain.dto.response.UserDto;
import jungmo.server.domain.entity.User;
import jungmo.server.domain.repository.UserRepository;
import jungmo.server.global.auth.dto.request.RegisterRequestDto;
import jungmo.server.global.auth.dto.response.SecurityUserDto;
import jungmo.server.global.auth.service.PrincipalDetails;
import jungmo.server.global.auth.service.S3Service;
import jungmo.server.global.error.ErrorCode;
import jungmo.server.global.error.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final S3Service s3Service;

    @Value("${cloud.aws.s3.default-profile-image}")
    private String defaultProfileImage;
    private final Random random = new Random();
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    /**
     * 유저코드로 유저를 찾는 로직
     * @param userCode
     * @return
     */
    @Transactional(readOnly = true)
    public UserDto findUser(UserCodeDto userCode) {
        User user = userRepository.findByUserCode(userCode.getUserCode())
                .orElseThrow(() ->new BusinessException(ErrorCode.USER_NOT_EXISTS));
        UserDto dto = user.toDto();
        return dto;
    }

    @Transactional(readOnly = true)
    public UserDto findUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_EXISTS));
        UserDto dto = user.toDto();
        return dto;
    }

    /**
     * 유저 수정 메서드
     * @param userDto
     * @return
     * @throws IOException
     */
    @Transactional
    public Long updateUserProfile(UserRequestDto userDto) throws IOException {
        User user = getUser();
        // 프로필 이미지 처리
        if (userDto.getProfileImage() != null) {
            if (userDto.getProfileImage().isEmpty()) {
                // 이미지 삭제 요청 → 기본 이미지 설정
                user.setProfileImage(defaultProfileImage);
            } else {
                // 새로운 이미지 업로드
                String profileImageUrl = s3Service.uploadFile(userDto.getProfileImage(), user.getId());
                user.setProfileImage(profileImageUrl);
            }
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
    public UserDto getUserInfo() {
        User user = getUser();
        return user.toDto();
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
                .profileImage(defaultProfileImage) // 기본 프로필 이미지 설정
                .build();

        return userRepository.save(user);
    }

    // 고유 코드 생성 (6자리 랜덤 문자열)
    private String generateUniqueUserCode() {
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

    private User getUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
        SecurityUserDto securityUser = SecurityUserDto.from(principalDetails);
        Long userId = securityUser.getUserId();

        // 데이터베이스에서 사용자 조회
        return userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_EXISTS));
    }
}
