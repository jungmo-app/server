package jungmo.server.domain.auth.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jungmo.server.domain.auth.dto.LoginRequestDto;
import jungmo.server.domain.auth.dto.RegisterRequestDto;
import jungmo.server.domain.auth.dto.TokenResponseDto;
import jungmo.server.domain.auth.service.AuthService;
import jungmo.server.domain.auth.service.RedisService;
import jungmo.server.global.error.ErrorCode;
import jungmo.server.global.error.exception.AuthenticateException;
import jungmo.server.global.util.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final JwtTokenProvider jwtTokenProvider;
    private final RedisService redisService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody @Valid RegisterRequestDto request) {
        authService.register(request);
        return ResponseEntity.ok("User registered successfully");
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponseDto> login(@RequestBody @Valid LoginRequestDto request, HttpServletResponse response) {
        TokenResponseDto tokenResponse = authService.login(request);

        // 리프레시 토큰을 쿠키로 설정
        Cookie refreshTokenCookie = new Cookie("refreshToken", tokenResponse.getRefreshToken());
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setSecure(true); // HTTPS 환경에서만 사용
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setMaxAge(60 * 60 * 24 * 14); // 14일 TTL

        response.addCookie(refreshTokenCookie);

        // 액세스 토큰만 JSON 응답으로 반환
        return ResponseEntity.ok(new TokenResponseDto(tokenResponse.getAccessToken(), null));
    }

    @PostMapping("/token/refresh")
    public ResponseEntity<TokenResponseDto> refreshToken(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = jwtTokenProvider.resolveRefreshTokenFromCookie(request);

        if (refreshToken == null || !redisService.validateRefreshToken(jwtTokenProvider.getEmailFromToken(refreshToken), refreshToken)) {
            throw new AuthenticateException(ErrorCode.INVALID_REFRESH_TOKEN);
        }

        String email = jwtTokenProvider.getEmailFromToken(refreshToken);
        String newAccessToken = jwtTokenProvider.generateAccessToken(email);
        String newRefreshToken = jwtTokenProvider.generateRefreshToken(email);

        // 리프레시 토큰 갱신
        redisService.saveRefreshToken(email, newRefreshToken);

        // 새로운 리프레시 토큰을 쿠키에 설정
        Cookie refreshTokenCookie = new Cookie("refreshToken", newRefreshToken);
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setSecure(true);
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setMaxAge(60 * 60 * 24 * 14);

        response.addCookie(refreshTokenCookie);

        return ResponseEntity.ok(new TokenResponseDto(newAccessToken, null));
    }
}

