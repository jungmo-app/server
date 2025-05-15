package jungmo.server.global.config;

import jungmo.server.global.interceptor.ChatHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketTransportRegistration;

@Configuration
@RequiredArgsConstructor
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final ChatHandler chatHandler;

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry
            .addEndpoint("/connection")
            .setAllowedOriginPatterns("*")
            .withSockJS();
        registry
            .addEndpoint("/connection")
            .setAllowedOriginPatterns("*");
        registry.setPreserveReceiveOrder(true);     // 메시지 순서 보장 ?
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.setApplicationDestinationPrefixes("/app");
        registry.enableSimpleBroker("/topic", "/queue");
        registry.setPreservePublishOrder(true);     // 메시지 순서 보장 ?
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(chatHandler);
    }

    /**
     * 하나의 WebSocket 메시지가 가질 수 있는 최대 바이트 크기 지정
     * - 기본값 (64 * 1024) 약 64KB
     * - 현재 설정 4 * 8192 약 32KB
     * -> 텍스트/바이너리 메시지가 매우 크면 서버에서 메모리 과부하나 예외를 막기 위해 제한
     *
     * @param registry WebSocket 전송 레벨 설정 제어 객체
     */
    @Override
    public void configureWebSocketTransport(WebSocketTransportRegistration registry) {
        registry.setMessageSizeLimit(4 * 8192);
    }
}
