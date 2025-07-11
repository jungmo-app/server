package jungmo.server.global.auth.service;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import io.lettuce.core.RedisException;
import jakarta.servlet.http.HttpServletResponse;
import jungmo.server.domain.dto.request.PasswordResetRequest;
import jungmo.server.domain.repository.UserRepository;
import jungmo.server.domain.service.EmailService;
import jungmo.server.domain.service.UserService;
import jungmo.server.global.auth.dto.request.KakaoTokenRequest;
import jungmo.server.global.auth.dto.request.LoginRequestDto;
import jungmo.server.global.auth.dto.request.RegisterRequestDto;
import jungmo.server.global.auth.dto.response.AccessTokenResponse;
import jungmo.server.global.auth.dto.response.KakaoUserResponse;
import jungmo.server.global.auth.dto.response.TokenResponseDto;
import jungmo.server.global.auth.dto.response.UserLoginResponse;
import jungmo.server.global.error.ErrorCode;
import jungmo.server.global.error.exception.BusinessException;
import jungmo.server.global.util.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import jungmo.server.domain.entity.User;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;
    private final RedisService redisService;
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final KakaoService kakaoService;

    @Transactional
    public UserLoginResponse register(RegisterRequestDto request, HttpServletResponse response) {
        // 비밀번호 암호화 후 UserService에 위임하여 사용자 생성
        String encodedPassword = passwordEncoder.encode(request.getPassword());
        User user = userService.createUser(request, encodedPassword);

        // Authentication 생성 및 SecurityContext에 저장
        saveAuthentication(user);

        String accessToken = jwtTokenProvider.generateAccessToken(user.getEmail());
        String refreshToken = jwtTokenProvider.generateRefreshToken(user.getEmail());

        //  Redis에 Refresh Token 저장 (만료 시간 적용)
        redisService.saveRefreshToken(user.getEmail(), refreshToken, jwtTokenProvider.getRefreshTokenExpiration());

        UserLoginResponse userResponse = new UserLoginResponse(user.getId(), user.getUserCode(), user.getUserName(), user.getProfileImage(), user.getProvider(),accessToken);

        ResponseCookie refreshTokenCookie = ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .secure(true)
                .sameSite("None")  //  크로스 도메인 요청 허용
                .domain("jungmoserver.shop")  //  쿠키가 전송될 도메인 설정
                .path("/")
                .maxAge((int) (jwtTokenProvider.getRefreshTokenExpiration() / 1000))
                .build();

        response.addHeader("Set-Cookie", refreshTokenCookie.toString());
        return userResponse;
    }

    private void  saveAuthentication(User user) {
        UserDetails userDetails = new PrincipalDetails(user); // PrincipalDetails는 UserDetails 구현체
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities()
        );
        // SecurityContext에 인증 정보 저장
        SecurityContextHolder.getContext().setAuthentication(authentication);

    }

    /**
     * 로그인 처리
     */
    public UserLoginResponse login(LoginRequestDto request, HttpServletResponse response) {
        try {
            // 1. 이메일과 비밀번호를 기반으로 인증
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );

            // 2. 인증 성공 후 Principal에서 사용자 정보 추출
            PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
            User user = principal.getUser();
            String email = principal.getUsername();

            if (user.getIsDeleted()) {
                throw new BusinessException(ErrorCode.USER_DELETED);
            }

            // 3. Access Token과 Refresh Token 생성
            String accessToken = jwtTokenProvider.generateAccessToken(email);
            String refreshToken = jwtTokenProvider.generateRefreshToken(email);

            // 4. Redis에 Refresh Token 저장 (만료 시간 적용)
            redisService.saveRefreshToken(email, refreshToken, jwtTokenProvider.getRefreshTokenExpiration());
            UserLoginResponse userResponse = new UserLoginResponse(user.getId(), user.getUserCode(), user.getUserName(), user.getProfileImage(), user.getProvider(),accessToken);

            ResponseCookie refreshTokenCookie = ResponseCookie.from("refreshToken", refreshToken)
                    .httpOnly(true)
                    .secure(true)
                    .sameSite("None")  //  크로스 도메인 요청 허용
                    .domain("jungmoserver.shop")  //  쿠키가 전송될 도메인 설정
                    .path("/")
                    .maxAge((int) (jwtTokenProvider.getRefreshTokenExpiration() / 1000))
                    .build();

            response.addHeader("Set-Cookie", refreshTokenCookie.toString());
            return userResponse;

        } catch (AuthenticationException e) {
            throw new BusinessException(ErrorCode.BAD_CREDENTIALS);
        }
    }

    @Transactional
    public TokenResponseDto testLogin(String email) {
        try {
            User user = userRepository.findByEmail(email).orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_EXISTS));

            if (user.getIsDeleted()) {
                throw new BusinessException(ErrorCode.USER_DELETED);
            }

            // 3. Access Token과 Refresh Token 생성
            String accessToken = jwtTokenProvider.generateAccessToken(email);
            String refreshToken = jwtTokenProvider.generateRefreshToken(email);

            // 4. Redis에 Refresh Token 저장 (만료 시간 적용)
            redisService.saveRefreshToken(email, refreshToken, jwtTokenProvider.getRefreshTokenExpiration());

            TokenResponseDto tokenResponseDto = new TokenResponseDto();
            tokenResponseDto.setAccessToken(accessToken);
            tokenResponseDto.setRefreshToken(refreshToken);
            return tokenResponseDto;

        } catch (AuthenticationException e) {
            throw new BusinessException(ErrorCode.BAD_CREDENTIALS);
        }
    }

    @Transactional
    public UserLoginResponse kakaoAppLogin(KakaoTokenRequest kakaoToeknRequest, HttpServletResponse response) {
        KakaoUserResponse userInfo = kakaoService.getUserEmail(kakaoToeknRequest.getToken());

        User user = userRepository.findByEmail(userInfo.getEmail()).orElse(null);

        if (user != null) {
            // 4. 소프트 딜리트 상태 확인
            if (user.getIsDeleted()) {
                // 탈퇴된 사용자 복구
                user.reactivate(userInfo.getKakaoId().toString(),"kakao"); // 상태를 ACTIVE로 변경
                userRepository.save(user);
            }
        } else {
            // 고유 코드 생성
            String uniqueCode = userService.generateUniqueUserCode();
            // 5. 신규 사용자 저장
            user = userRepository.save(User.builder()
                    .email(userInfo.getEmail())
                    .userCode(uniqueCode)
                    .oauthId(userInfo.getKakaoId().toString())
                    .userName(userInfo.getNickName())
                    .provider("kakao")
                    .role("ROLE_USER")
                    .build());

        }

        //시큐리티 컨텍스트에 유저정보 저장
        saveAuthentication(user);

        String accessToken = jwtTokenProvider.generateAccessToken(user.getEmail());
        String refreshToken = jwtTokenProvider.generateRefreshToken(userInfo.getEmail());

        // Redis에 Refresh Token 저장
        redisService.saveRefreshToken(userInfo.getEmail(), refreshToken, jwtTokenProvider.getRefreshTokenExpiration());

        UserLoginResponse userResponse = new UserLoginResponse(user.getId(), user.getUserCode(), user.getUserName(), user.getProfileImage(), user.getProvider(),accessToken);

        ResponseCookie refreshTokenCookie = ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .secure(true)
                .sameSite("None")  //  크로스 도메인 요청 허용
                .domain("jungmoserver.shop")  //  쿠키가 전송될 도메인 설정
                .path("/")
                .maxAge((int) (jwtTokenProvider.getRefreshTokenExpiration() / 1000))
                .build();

        response.addHeader("Set-Cookie", refreshTokenCookie.toString());
        return userResponse;
    }

    @Transactional
    public void processPasswordResetRequest(PasswordResetRequest request) {
        String email = request.getEmail();
        // 1. 사용자 확인
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_EXISTS));

        // 2. 재설정 토큰 생성 및 저장
        String resetToken = UUID.randomUUID().toString(); // 랜덤 토큰 생성
        user.setResetToken(resetToken);
        user.setResetTokenExpiry(LocalDateTime.now().plusHours(1)); // 토큰 만료 시간 설정

        String resetLink = "https://front.jungmoserver.shop/reset-password?token=" + resetToken;
        emailService.sendEmail(email, resetLink);
    }

    @Transactional
    public void resetPassword(String token, String newPassword) {
        // 1. 토큰 검증
        User user = userRepository.findByResetToken(token)
                .orElseThrow(() -> new BusinessException(ErrorCode.INVALID_TOKEN));

        if (user.getResetTokenExpiry().isBefore(LocalDateTime.now())) {
            throw new BusinessException(ErrorCode.TOKEN_EXPIRED);
        }

        // 2. 비밀번호 업데이트
        user.resetPassword(passwordEncoder.encode(newPassword));

    }

    /**
     * Refresh Token을 사용한 Access Token 재발급
     */
    public AccessTokenResponse refreshToken(String refreshToken, HttpServletResponse response) {
        try {
            // 1. Refresh Token에서 이메일 추출
            String email = jwtTokenProvider.getEmailFromToken(refreshToken);

            // 2. Redis에서 저장된 Refresh Token 가져오기
            String savedRefreshToken = redisService.getRefreshToken(email);

            // 3. Refresh Token 검증
            if (savedRefreshToken == null || !savedRefreshToken.equals(refreshToken)) {
                throw new BusinessException(ErrorCode.INVALID_REFRESH_TOKEN);
            }

            // 4. 새로운 Access Token과 Refresh Token 생성
            String newAccessToken = jwtTokenProvider.generateAccessToken(email);
            String newRefreshToken = jwtTokenProvider.generateRefreshToken(email);

            // 5. Redis에 새로운 Refresh Token 저장
            redisService.saveRefreshToken(email, newRefreshToken, jwtTokenProvider.getRefreshTokenExpiration());

            // 6. 쿠키에 새로운 토큰 저장

            ResponseCookie refreshTokenCookie = ResponseCookie.from("refreshToken", newRefreshToken)
                    .httpOnly(true)
                    .secure(true)
                    .sameSite("None")  //  크로스 도메인 요청 허용
                    .domain("jungmoserver.shop")  //  쿠키가 전송될 도메인 설정
                    .path("/")
                    .maxAge((int) (jwtTokenProvider.getRefreshTokenExpiration() / 1000))
                    .build();

            response.addHeader("Set-Cookie", refreshTokenCookie.toString());
            return new AccessTokenResponse(newAccessToken);

        } catch (ExpiredJwtException e) {
            throw new BusinessException(ErrorCode.TOKEN_EXPIRED);
        } catch (MalformedJwtException | SignatureException e) {
            throw new BusinessException(ErrorCode.INVALID_REFRESH_TOKEN);
        } catch (RedisException e) { // Redis 예외 처리
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR);
        }catch (Exception e) {
            throw new BusinessException(ErrorCode.INVALID_REFRESH_TOKEN);
        }
    }

}
