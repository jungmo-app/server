package jungmo.server.global.oauth2.handler;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jungmo.server.global.auth.service.RedisService;
import jungmo.server.global.util.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomOAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final JwtTokenProvider jwtTokenProvider;
    private final RedisService redisService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        log.info("OAuth2 User Attributes: {}",oAuth2User.getAttributes());
        String email = oAuth2User.getAttribute("email");

        // JWT 생성
        String refreshToken = jwtTokenProvider.generateRefreshToken(email);

        // Redis에 Refresh Token 저장
        redisService.saveRefreshToken(email, refreshToken, jwtTokenProvider.getRefreshTokenExpiration());

        String domain = request.getServerName();
        log.info("resolved domain from request: {}", domain);

        ResponseCookie refreshTokenCookie = ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .secure(true)
                .sameSite("None")  //  크로스 도메인 요청 허용
                .domain(domain)  //  쿠키가 전송될 도메인 설정
                .path("/")
                .maxAge((int) (jwtTokenProvider.getRefreshTokenExpiration() / 1000))
                .build();

        response.addHeader("Set-Cookie", refreshTokenCookie.toString());


        // 로그인 성공 후 리디렉트
        response.sendRedirect("https://front.jungmoserver.shop/login/oauth2");
    }

}
