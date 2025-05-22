package jungmo.server.domain.handler.chat;

import jungmo.server.domain.dto.request.chat.ChattingMessageRequest;
import jungmo.server.domain.entity.User;
import jungmo.server.domain.entity.chat.MessageType;
import jungmo.server.domain.service.chat.ChatMessageSaveService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ImageMessageHandler implements ChatMessageHandler{

    private final ChatMessageSaveService chatMessageSaveService;

    @Override
    public MessageType getMessageType() {
        return MessageType.IMAGE;
    }

    @Override
    public void handleMessage(ChattingMessageRequest request, Long roomId, User user) {
        chatMessageSaveService.saveImageMessage(request, roomId, user);
    }
}
