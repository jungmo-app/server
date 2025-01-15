package jungmo.server.global.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jungmo.server.global.error.ErrorCode;
import jungmo.server.global.error.exception.CustomJwtException;
import jungmo.server.global.util.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String token = jwtTokenProvider.resolveToken(request);

        if (token != null) {
            try {
                // 블랙리스트 확인 로직 추가
                if (isTokenBlacklisted(token)) {
                    throw new CustomJwtException(ErrorCode.ALREADY_LOGOUT_TOKEN);
                }

                jwtTokenProvider.validateToken(token); // 토큰 검증
                Authentication auth = jwtTokenProvider.getAuthentication(token);
                SecurityContextHolder.getContext().setAuthentication(auth);
            } catch (CustomJwtException e) {
                // CustomJwtException을 Spring Security의 AuthenticationException으로 전환
                throw new AuthenticationException(e.getMessage(), e) {};
            }
        }

        filterChain.doFilter(request, response);
    }
    /**
     * 블랙리스트에 있는 토큰인지 확인
     */
    private boolean isTokenBlacklisted(String token) {
        return redisTemplate.hasKey("BLACKLIST:" + token); // Redis에서 블랙리스트 확인
    }
}