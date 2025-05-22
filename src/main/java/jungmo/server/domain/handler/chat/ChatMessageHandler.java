package jungmo.server.domain.handler.chat;

import jungmo.server.domain.dto.request.chat.ChattingMessageRequest;
import jungmo.server.domain.entity.User;
import jungmo.server.domain.entity.chat.MessageType;

public interface ChatMessageHandler {

    MessageType getMessageType();
    void handleMessage(ChattingMessageRequest request, Long roomId, User user);

}
