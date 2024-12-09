package jungmo.server.domain.auth.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jungmo.server.domain.auth.dto.LoginRequest;
import jungmo.server.domain.auth.dto.RegisterRequest;
import jungmo.server.domain.auth.dto.TokenResponse;
import jungmo.server.domain.auth.service.AuthService;
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

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody @Valid RegisterRequest request) {
        authService.register(request);
        return ResponseEntity.ok("User registered successfully");
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@RequestBody @Valid LoginRequest request, HttpServletResponse response) {
        TokenResponse tokenResponse = authService.login(request);

        // 리프레시 토큰을 쿠키로 설정
        Cookie refreshTokenCookie = new Cookie("refreshToken", tokenResponse.getRefreshToken());
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setSecure(true); // HTTPS 환경에서만 사용
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setMaxAge(60 * 60 * 24 * 14); // 14일 TTL

        response.addCookie(refreshTokenCookie);

        // 액세스 토큰만 JSON 응답으로 반환
        return ResponseEntity.ok(new TokenResponse(tokenResponse.getAccessToken(), null));
    }
}

