package jungmo.server.global.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Arrays;
import jungmo.server.global.auth.service.PrincipalDetails;
import jungmo.server.global.error.ErrorCode;
import jungmo.server.global.error.exception.CustomJwtException;
import jungmo.server.global.util.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import software.amazon.awssdk.utils.IoUtils;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();

        if (path.equals("/auth/refresh")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = jwtTokenProvider.resolveToken(request);

        if (token != null) {
            try {

                if (!jwtTokenProvider.verifyToken(token)) {  // 변조된 토큰은 false 반환
                    throw new JwtException("Invalid token");
                }

                // 블랙리스트 확인
                if (jwtTokenProvider.isTokenBlacklisted(token)) {
                    throw new CustomJwtException(ErrorCode.ALREADY_LOGOUT_TOKEN);
                }

                Authentication auth = jwtTokenProvider.getAuthentication(token);
                log.info("✅ JWT 인증 성공 - Authentication: {}", auth);

                SecurityContextHolder.getContext().setAuthentication(auth);
                log.debug("✅ SecurityContextHolder에 Authentication 설정 완료: {}", auth);

            } catch (ExpiredJwtException e) {
                log.warn("Expired JWT token: {}", e.getMessage());
                handleInvalidToken(response, "A002", "Access token is expired");
                return;
            } catch (JwtException e) {
                log.warn("Invalid JWT token: {}", e.getMessage());
                handleInvalidToken(response, "A003", "Invalid token");
                return;
            } catch (CustomJwtException e) {
                log.warn("Blacklisted JWT token: {}", e.getMessage());
                handleInvalidToken(response, "A004", "BlackListToken token");
                return;
            }
        } else {
            log.warn("🚨 JWT 없음 또는 유효하지 않음 - SecurityContext 초기화");
            SecurityContextHolder.clearContext();  // ✅ 비로그인 사용자는 SecurityContext 초기화
        }

        filterChain.doFilter(request, response);
    }

    private void handleInvalidToken(HttpServletResponse response, String code, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("status", HttpServletResponse.SC_UNAUTHORIZED);
        errorResponse.put("code", code);
        errorResponse.put("message", message);

        objectMapper.writeValue(response.getWriter(), errorResponse);
    }


}