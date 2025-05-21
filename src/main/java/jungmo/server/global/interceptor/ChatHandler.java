package jungmo.server.global.interceptor;

import jakarta.annotation.Nullable;
import java.util.Objects;
import jungmo.server.global.error.ErrorCode;
import jungmo.server.global.error.exception.CustomJwtException;
import jungmo.server.global.util.JwtTokenProvider;
import jungmo.server.global.util.jwt.JwtProvider;
import jungmo.server.global.util.jwt.WebSocketTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.web.socket.messaging.SessionConnectedEvent;

@Slf4j
@Configuration
@RequiredArgsConstructor
@Order(Ordered.HIGHEST_PRECEDENCE + 99)
public class ChatHandler implements ChannelInterceptor {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public Message<?> preSend(@Nullable Message<?> message, @Nullable MessageChannel channel) {

        StompHeaderAccessor accessor = null;

        if (Objects.nonNull(message)) {
            accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        }

        if (Objects.nonNull(accessor)) {

            if (StompCommand.CONNECT.equals(accessor.getCommand())) {
                String token = String.valueOf(accessor.getNativeHeader("Authorization"));

                JwtProvider jwtProvider = WebSocketTokenProvider.of(token);

                String accessToken = jwtProvider.resolveToken();

                if (!jwtTokenProvider.verifyToken(token)) {
                    throw new JwtException("Invalid token");
                }

                if (jwtTokenProvider.isTokenBlacklisted(token)) {
                    throw new CustomJwtException(ErrorCode.ALREADY_LOGOUT_TOKEN);
                }

                Authentication auth = jwtTokenProvider.getAuthentication(accessToken);

                accessor.setUser(auth);
            }

        }
        return message;
    }

    //TODO : 로그인 시 이 부분에서 Redis에 담아줄 듯? CONNECT -> accessor에 setUser 해주고
    @EventListener
    public void socketConnectedEventHandler(SessionConnectedEvent event) {
        System.out.println("Socket connected :" + event);
        System.out.println("Socket connected user : " + event.getUser());
        System.out.println("Socket connected message : " + event.getMessage());
    }
}
