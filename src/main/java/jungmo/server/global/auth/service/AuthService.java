package jungmo.server.global.auth.service;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jungmo.server.domain.repository.UserRepository;
import jungmo.server.global.auth.dto.request.LoginRequestDto;
import jungmo.server.global.auth.dto.request.RefreshTokenRequestDto;
import jungmo.server.global.auth.dto.request.RegisterRequestDto;
import jungmo.server.global.auth.dto.response.TokenResponseDto;
import jungmo.server.global.util.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import jungmo.server.domain.entity.User;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;
    private final RedisService redisService;

    @Transactional
    public void register(RegisterRequestDto request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already in use");
        }

        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .userName(request.getName())
                .provider("email")
                .role("ROLE_USER")
                .build();

        userRepository.save(user);
        // Authentication 생성 및 SecurityContext에 저장
        saveAuthentication(user);
    }

    private void saveAuthentication(User user) {
        UserDetails userDetails = new PrincipalDetails(user); // PrincipalDetails는 UserDetails 구현체
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities()
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    /**
     * 로그인 처리
     */
    public void login(LoginRequestDto request, HttpServletResponse response) {
        try {
            // 1. 이메일과 비밀번호를 기반으로 인증
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );

            // 2. 인증 성공 후 Principal에서 사용자 정보 추출
            PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
            String email = principal.getUsername();

            // 3. Access Token과 Refresh Token 생성
            String accessToken = jwtTokenProvider.generateAccessToken(email);
            String refreshToken = jwtTokenProvider.generateRefreshToken(email);

            // 4. Redis에 Refresh Token 저장 (만료 시간 적용)
            redisService.saveRefreshToken(email, refreshToken, jwtTokenProvider.getRefreshTokenExpiration());

            // 5. Access Token과 Refresh Token을 쿠키에 저장
            addCookie(response, "accessToken", accessToken, (int) jwtTokenProvider.getAccessTokenExpiration());
            addCookie(response, "refreshToken", refreshToken, (int) jwtTokenProvider.getRefreshTokenExpiration());

        } catch (AuthenticationException e) {
            throw new IllegalArgumentException("Invalid email or password", e);
        }
    }


    /**
     * Refresh Token을 사용한 Access Token 재발급
     */
    public void refreshToken(RefreshTokenRequestDto request, HttpServletResponse response) {
        // 1. Refresh Token에서 이메일 추출
        String email = jwtTokenProvider.getEmailFromToken(request.getRefreshToken());

        // 2. Redis에서 저장된 Refresh Token 가져오기
        String savedRefreshToken = redisService.getRefreshToken(email);

        // 3. Refresh Token 검증
        if (savedRefreshToken == null || !savedRefreshToken.equals(request.getRefreshToken())) {
            throw new IllegalArgumentException("Invalid refresh token");
        }

        // 4. 새로운 Access Token과 Refresh Token 생성
        String newAccessToken = jwtTokenProvider.generateAccessToken(email);
        String newRefreshToken = jwtTokenProvider.generateRefreshToken(email);

        // 5. Redis에 새로운 Refresh Token 저장
        redisService.saveRefreshToken(email, newRefreshToken, jwtTokenProvider.getRefreshTokenExpiration());

        // 6. 쿠키에 새로운 토큰 저장
        addCookie(response, "accessToken", newAccessToken, (int) jwtTokenProvider.getAccessTokenExpiration());
        addCookie(response, "refreshToken", newRefreshToken, (int) jwtTokenProvider.getRefreshTokenExpiration());

    }

    private void addCookie(HttpServletResponse response, String name, String value, int maxAge) {
        Cookie cookie = new Cookie(name, value);
        cookie.setHttpOnly(false); // 개발중에는 false로 해서 프론트측에서 접근할수있도록
        cookie.setMaxAge(maxAge); // 쿠키 만료 시간 설정
        cookie.setPath("/"); // 모든 경로에서 쿠키 사용 가능
        response.addCookie(cookie);
    }

}
