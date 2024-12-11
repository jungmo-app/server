package jungmo.server.global.auth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RedisService {

    private final RedisTemplate<String, Object> redisTemplate;

    public void saveRefreshToken(String email, String refreshToken) {
        redisTemplate.opsForValue().set(email, refreshToken, 14, TimeUnit.DAYS);
    }

    public String getRefreshToken(String email) {
        Object token = redisTemplate.opsForValue().get(email);
        return token != null ? token.toString() : null;
    }

    public boolean validateRefreshToken(String email, String refreshToken) {
        String storedToken = (String) redisTemplate.opsForValue().get(email);
        return storedToken != null && storedToken.equals(refreshToken);
    }

    public void deleteRefreshToken(String email) {
        redisTemplate.delete(email);
    }
}
