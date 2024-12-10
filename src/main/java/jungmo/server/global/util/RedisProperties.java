package jungmo.server.global.util;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties("spring.data.redis")
public class RedisProperties {
    private String host;
    private int port;
    private String password;
}

