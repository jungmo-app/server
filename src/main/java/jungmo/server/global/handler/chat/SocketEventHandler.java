package jungmo.server.global.handler.chat;

import java.security.Principal;
import java.util.Objects;
import jungmo.server.domain.service.chat.ChatRedisService;
import jungmo.server.global.auth.service.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Component
@RequiredArgsConstructor
public class SocketEventHandler {

    private final ChatRedisService chatRedisService;

    @EventListener
    public void handleConnectEvent(SessionConnectedEvent event) {
        Principal principal = event.getUser();

        if (principal instanceof Authentication auth &&
            auth.getPrincipal() instanceof PrincipalDetails principalDetails) {

            String sessionId = Objects.requireNonNull(event
                .getMessage()
                .getHeaders()
                .get("simpSessionId")).toString();

            Long userId = principalDetails.getUser().getId();
            chatRedisService.registerSession(sessionId, userId);
        }
    }

    @EventListener
    public void handleDisconnectEvent(SessionDisconnectEvent event) {
        chatRedisService.unregisterSession(event.getSessionId());
    }
}
