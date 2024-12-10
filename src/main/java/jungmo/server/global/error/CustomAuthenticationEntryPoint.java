package jungmo.server.global.error;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jungmo.server.global.error.exception.CustomJwtException;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401 상태 설정
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        // ErrorResponse 생성 및 반환
        ErrorResponse errorResponse = determineErrorResponse(authException);
        new ObjectMapper().writeValue(response.getWriter(), errorResponse);
    }
    private ErrorResponse determineErrorResponse(AuthenticationException authException) {
        // AuthenticationException의 원인이 CustomJwtException인지 확인
        if (authException.getCause() instanceof CustomJwtException) {
            CustomJwtException jwtException = (CustomJwtException) authException.getCause();
            return ErrorResponse.of(jwtException.getErrorCode()); // JWT 관련 ErrorCode 반환
        }

        // 기본 인증 실패 응답
        return ErrorResponse.of(ErrorCode.AUTHENTICATION_FAILED);
    }
}
