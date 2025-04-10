package jungmo.server.global.util;

import io.jsonwebtoken.*;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import jungmo.server.domain.entity.User;
import jungmo.server.domain.repository.UserRepository;
import jungmo.server.global.auth.service.CustomUserDetailsService;
import jungmo.server.global.auth.service.PrincipalDetails;
import jungmo.server.global.error.ErrorCode;
import jungmo.server.global.error.exception.BusinessException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
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
    private final UserRepository userRepository;

    private final long accessTokenExpiration = 1000 * 60; //1t분
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
        String email = getEmailFromToken(refreshToken);

        String storedToken = redisTemplate.opsForValue().get(email);
        log.info("refreshToken: {}",refreshToken);
        log.info("storedToken: {}",storedToken);
        if (storedToken == null || !storedToken.replaceAll("\"","").equals(refreshToken)) {
            throw new BusinessException(ErrorCode.INVALID_REFRESH_TOKEN);
        }

        redisTemplate.delete(email);


        // 2. 액세스 토큰 블랙리스트 등록
        long expiration = getExpiration(accessToken); // 토큰 만료 시간 계산
        if (expiration > 0) {
            redisTemplate.opsForValue().set("BLACKLIST:" + accessToken, "logged_out", Duration.ofMillis(expiration));
        }
    }

    public Boolean isTokenBlacklisted(String token) {
        return Boolean.TRUE.equals(redisTemplate.hasKey("BLACKLIST:" + token));
    }


    // 토큰에서 Authentication 객체 생성
    public Authentication getAuthentication(String token) {
        String email = getEmailFromToken(token);
        // 데이터베이스에서 사용자 직접 조회 (소셜 로그인 포함)
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_EXISTS));

        PrincipalDetails principalDetails = new PrincipalDetails(user); // ✅ PrincipalDetails 직접 생성

        // ✅ 사용자의 역할(Role) 정보를 직접 리스트로 생성
        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(user.getRole()));

        return new UsernamePasswordAuthenticationToken(principalDetails, null, authorities);
    }


    public boolean verifyToken(String token) {
        try {
            Jws<Claims> claims = Jwts.parser()
                    .setSigningKey(secretKey)
                    .parseClaimsJws(token);

            Date expiration = claims.getBody().getExpiration();
            if (expiration.before(new Date())) {
                throw new ExpiredJwtException(null, claims.getBody(), "Token is expired");  // ✅ 만료된 경우 예외 발생
            }

            return true; // 유효한 토큰이면 true 반환
        } catch (ExpiredJwtException e) {
            throw e; // 만료된 토큰이면 예외 던지기 (JwtAuthenticationFilter에서 처리)
        } catch (Exception e) {
            log.error("Invalid JWT signature: {}", token);
            return false; // 변조된 토큰이면 false 반환
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
