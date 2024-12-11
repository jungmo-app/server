package jungmo.server.global.auth.service;

import jungmo.server.domain.repository.UserRepository;
import jungmo.server.global.auth.dto.request.AuthorizationCodeRequest;
import jungmo.server.global.auth.dto.request.LoginRequestDto;
import jungmo.server.global.auth.dto.request.RefreshTokenRequestDto;
import jungmo.server.global.auth.dto.request.RegisterRequestDto;
import jungmo.server.global.auth.dto.response.KakaoUserInfo;
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
    private final KakaoService kakaoService;

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

    public TokenResponseDto login(LoginRequestDto request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );

            PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
            String email = principal.getUsername();

            String accessToken = jwtTokenProvider.generateAccessToken(email);
            String refreshToken = jwtTokenProvider.generateRefreshToken(email);

            redisService.saveRefreshToken(email, refreshToken);

            return new TokenResponseDto(accessToken, refreshToken);

        } catch (AuthenticationException e) {
            throw new IllegalArgumentException("Invalid email or password", e);
        }

    }

    public TokenResponseDto refreshToken(RefreshTokenRequestDto request) {
        String email = jwtTokenProvider.getEmailFromToken(request.getRefreshToken());

        String savedRefreshToken = redisService.getRefreshToken(email);
        if (savedRefreshToken == null || !savedRefreshToken.equals(request.getRefreshToken())) {
            throw new IllegalArgumentException("Invalid refresh token");
        }

        String newAccessToken = jwtTokenProvider.generateAccessToken(email);
        String newRefreshToken = jwtTokenProvider.generateRefreshToken(email);

        redisService.saveRefreshToken(email, newRefreshToken);

        return new TokenResponseDto(newAccessToken, newRefreshToken);
    }

    public TokenResponseDto authenticateWithKakao(AuthorizationCodeRequest request) {
        // 카카오 사용자 정보 가져오기
        KakaoUserInfo userInfo = kakaoService.getUserInfo(request.getAuthorizationCode());

        // JWT 생성
        String accessToken = jwtTokenProvider.generateAccessToken(userInfo.getEmail());
        String refreshToken = jwtTokenProvider.generateRefreshToken(userInfo.getEmail());
        redisService.saveRefreshToken(userInfo.getEmail(), refreshToken);

        // Authentication 생성 및 SecurityContext에 저장
        User user = userRepository.findByEmail(userInfo.getEmail())
                .orElseGet(() -> {
                    // 신규 사용자 생성 및 저장
                    User newUser = User.builder()
                            .email(userInfo.getEmail())
                            .userName(userInfo.getNickname())
                            .password("KAKAO") // 임의 값 설정 (카카오는 비밀번호가 없음)
                            .role("ROLE_USER")
                            .provider("kakao")
                            .build();
                    return userRepository.save(newUser);
                });

        saveAuthentication(user);

        return new TokenResponseDto(accessToken, refreshToken);
    }

}
