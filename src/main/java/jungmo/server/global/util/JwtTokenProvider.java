package jungmo.server.global.util;

import io.jsonwebtoken.*;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jungmo.server.global.auth.service.CustomUserDetailsService;
import jungmo.server.global.error.ErrorCode;
import jungmo.server.global.error.exception.CustomJwtException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Base64;
import java.util.Date;
import java.util.List;

@Slf4j
@Getter
@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    private final JwtProperties jwtProperties;
    private String secretKey;
    private final CustomUserDetailsService userDetailsService;
    private final RedisTemplate<String,String> redisTemplate;

    private final long accessTokenExpiration = 1000 * 60 * 30; //30분
    private final long refreshTokenExpiration = 1000 * 60 * 60 * 24 * 7;  //7일

    @PostConstruct
    protected void init() {
        secretKey = Base64.getEncoder().encodeToString(jwtProperties.getSecret().getBytes());
    }

    public String generateAccessToken(String email) {
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + accessTokenExpiration))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    public String generateRefreshToken(String email) {
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + refreshTokenExpiration))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    public void invalidateTokens(String accessToken, String refreshToken) {
        // 1. 리프레시 토큰 삭제
        if (refreshToken != null) {
            redisTemplate.delete(refreshToken);
        }

        // 2. 액세스 토큰 블랙리스트 등록
        long expiration = getExpiration(accessToken); // 토큰 만료 시간 계산
        redisTemplate.opsForValue().set("BLACKLIST:" + accessToken, "logged_out", Duration.ofMillis(expiration));
    }

    // 토큰에서 Authentication 객체 생성
    public Authentication getAuthentication(String token) {
        String email = getEmailFromToken(token);
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);
        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException e) {
            throw new CustomJwtException(ErrorCode.TOKEN_EXPIRED, e);
        } catch (MalformedJwtException e) {
            throw new CustomJwtException(ErrorCode.MALFORMED_TOKEN, e);
        } catch (UnsupportedJwtException e) {
            throw new CustomJwtException(ErrorCode.UNSUPPORTED_TOKEN, e);
        } catch (SignatureException e) {
            throw new CustomJwtException(ErrorCode.INVALID_TOKEN, e);
        } catch (Exception e) {
            throw new CustomJwtException(ErrorCode.INVALID_TOKEN, e);
        }
    }


    public String getEmailFromToken(String token) {
        return Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }


    public long getExpiration(String token) {
        return Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token)
                .getBody()
                .getExpiration()
                .getTime() - System.currentTimeMillis();
    }
}
