package jungmo.server.global.auth.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import io.lettuce.core.RedisException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jungmo.server.domain.service.UserService;
import jungmo.server.global.auth.dto.request.LoginRequestDto;
import jungmo.server.global.auth.dto.request.RefreshTokenRequestDto;
import jungmo.server.global.auth.dto.request.RegisterRequestDto;
import jungmo.server.global.error.ErrorCode;
import jungmo.server.global.error.exception.BusinessException;
import jungmo.server.global.util.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import jungmo.server.domain.entity.User;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;
    private final RedisService redisService;

    @Transactional
    public void register(RegisterRequestDto request, HttpServletResponse response) {
        // 비밀번호 암호화 후 UserService에 위임하여 사용자 생성
        String encodedPassword = passwordEncoder.encode(request.getPassword());
        User user = userService.createUser(request, encodedPassword);

        // Authentication 생성 및 SecurityContext에 저장
        saveAuthentication(user);

        String accessToken = jwtTokenProvider.generateAccessToken(user.getEmail());
        String refreshToken = jwtTokenProvider.generateRefreshToken(user.getEmail());

        //  Redis에 Refresh Token 저장 (만료 시간 적용)
        redisService.saveRefreshToken(user.getEmail(), refreshToken, jwtTokenProvider.getRefreshTokenExpiration());

        //  Access Token과 Refresh Token을 쿠키에 저장
        addCookie(response, "accessToken", accessToken, (int) jwtTokenProvider.getAccessTokenExpiration());
        addCookie(response, "refreshToken", refreshToken, (int) jwtTokenProvider.getRefreshTokenExpiration());

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
    public void login(LoginRequestDto request, HttpServletResponse response) {
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

            // 5. Access Token과 Refresh Token을 쿠키에 저장
            addCookie(response, "accessToken", accessToken, (int) jwtTokenProvider.getAccessTokenExpiration());
            addCookie(response, "refreshToken", refreshToken, (int) jwtTokenProvider.getRefreshTokenExpiration());

        } catch (AuthenticationException e) {
            throw new BusinessException(ErrorCode.BAD_CREDENTIALS);
        }
    }



    /**
     * Refresh Token을 사용한 Access Token 재발급
     */
    public void refreshToken(RefreshTokenRequestDto request, HttpServletResponse response) {
        try {
            // 1. Refresh Token에서 이메일 추출
            String email = jwtTokenProvider.getEmailFromToken(request.getRefreshToken());

            // 2. Redis에서 저장된 Refresh Token 가져오기
            String savedRefreshToken = redisService.getRefreshToken(email);

            // 3. Refresh Token 검증
            if (savedRefreshToken == null || !savedRefreshToken.equals(request.getRefreshToken())) {
                throw new BusinessException(ErrorCode.INVALID_REFRESH_TOKEN);
            }

            // 4. 새로운 Access Token과 Refresh Token 생성
            String newAccessToken = jwtTokenProvider.generateAccessToken(email);
            String newRefreshToken = jwtTokenProvider.generateRefreshToken(email);

            // 5. Redis에 새로운 Refresh Token 저장
            redisService.saveRefreshToken(email, newRefreshToken, jwtTokenProvider.getRefreshTokenExpiration());

            // 6. 쿠키에 새로운 토큰 저장
            addCookie(response, "accessToken", newAccessToken, (int) jwtTokenProvider.getAccessTokenExpiration());
            addCookie(response, "refreshToken", newRefreshToken, (int) jwtTokenProvider.getRefreshTokenExpiration());

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

    private void addCookie(HttpServletResponse response, String name, String value, int maxAge) {
        Cookie cookie = new Cookie(name, value);
        cookie.setHttpOnly(false); // 개발중에는 false로 해서 프론트측에서 접근할수있도록
        cookie.setMaxAge(maxAge); // 쿠키 만료 시간 설정
        cookie.setPath("/"); // 모든 경로에서 쿠키 사용 가능
        response.addCookie(cookie);
    }

}
