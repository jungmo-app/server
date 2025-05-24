package jungmo.server.global.handler.chat;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import jungmo.server.domain.service.chat.ChatRedisService;
import jungmo.server.global.error.ErrorCode;
import jungmo.server.global.error.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.AbstractSubProtocolEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;
import org.springframework.web.socket.messaging.SessionUnsubscribeEvent;

@Component
@RequiredArgsConstructor
public class SocketSubscriptionEventHandler {

    private final ChatRedisService chatRedisService;

    private static final String DEFAULT_DESTINATION = "/topic/room/";

    @EventListener
    public void subscribeRoomEvent(SessionSubscribeEvent event) {
        StompHeaderAccessor accessor = getAccessor(event);

        String destination = accessor.getDestination();
        String sessionId = accessor.getSessionId();

        Long roomId = parseRoomIdBySubscription(destination);

        chatRedisService.subscribeRoom(roomId, sessionId);
    }

    @EventListener
    public void unsubscribeRoomEvent(SessionUnsubscribeEvent event) {
        StompHeaderAccessor accessor = getAccessor(event);

        String destination = accessor.getDestination();
        String sessionId = accessor.getSessionId();

        Long roomId = parseRoomIdBySubscription(destination);

        chatRedisService.unsubscribeRoom(roomId, sessionId);
    }

    private Long parseRoomIdBySubscription(final String destination) {

        Pattern pattern = Pattern.compile(DEFAULT_DESTINATION + "(\\d+)");
        Matcher matcher = pattern.matcher(destination);

        if (matcher.find()) {
            return Long.valueOf(matcher.group(1));
        }

        throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE);
    }

    private StompHeaderAccessor getAccessor(AbstractSubProtocolEvent event) {
        return MessageHeaderAccessor.getAccessor(event.getMessage(), StompHeaderAccessor.class);
    }
}
