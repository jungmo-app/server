package jungmo.server.domain.handler.chat;

import jungmo.server.domain.dto.request.chat.ChattingMessageRequest;
import jungmo.server.domain.entity.User;
import jungmo.server.domain.entity.chat.MessageType;
import org.springframework.stereotype.Component;

@Component
public class EnterChattingRoomHandler implements ChatMessageHandler {

    @Override
    public MessageType getMessageType() {
        return MessageType.ENTER;
    }

    @Override
    public void handleMessage(ChattingMessageRequest request, Long roomId, User user) {
        // 입장 처리와 동시에 메시지 읽음 표시 카운팅 ?
    }
}
