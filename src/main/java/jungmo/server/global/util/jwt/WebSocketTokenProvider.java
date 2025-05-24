package jungmo.server.global.util.jwt;

import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.ServerHttpRequest;

@Slf4j
public class WebSocketTokenProvider implements JwtProvider {

    private final String token;

    private WebSocketTokenProvider(String token) {
        this.token = token;
    }

    public static WebSocketTokenProvider of(String token) {
        return new WebSocketTokenProvider(token);
    }

    @Override
    public String resolveToken() {
        String bearerToken = token;
        if (Objects.nonNull(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

}
