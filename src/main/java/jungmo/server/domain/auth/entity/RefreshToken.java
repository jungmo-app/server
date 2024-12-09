package jungmo.server.domain.auth.entity;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import java.io.Serializable;

@Getter
@AllArgsConstructor
@RedisHash(value = "jwtToken", timeToLive = 60 * 60 * 24 * 14)  // 14일 TTL
public class RefreshToken implements Serializable {

    @Id
    private String id; // Redis의 키로 사용 (이메일 또는 UUID)

    @Indexed
    private String accessToken; // 액세스 토큰

    private String refreshToken; // 리프레시 토큰

    public void updateAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
}

