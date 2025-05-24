package jungmo.server.domain.service.chat;

import jungmo.server.domain.dto.request.chat.ChattingMessageRequest;
import jungmo.server.domain.dto.response.chat.ChatMessageResponse;
import jungmo.server.domain.entity.User;
import jungmo.server.domain.entity.chat.ChattingMessage;
import jungmo.server.domain.entity.chat.MessageType;
import jungmo.server.domain.repository.chat.ChattingMessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ChatMessageHandleService {

    private final ChattingMessageRepository chattingMessageRepository;
    private final SimpMessageSendingOperations sendingOperations;

    private static final String DEFAULT_TOPIC = "/topic/room/";

    @Transactional
    public ChattingMessage saveMessage(ChattingMessageRequest req, Long roomId, User user) {
        ChattingMessage message = ChattingMessage.create(
            req.getMessageType(),
            roomId,
            user.getId(),
            user.getUserName(),
            req.getMessageType() == MessageType.MESSAGE ? req.getMessage() : null,
            req.getMessageType() == MessageType.IMAGE ? req.getImageUrls() : null,
            req.getSendTime()
        );

        return chattingMessageRepository.save(message);
    }

    public void sendMessage(ChattingMessage chattingMessage, Long roomId) {

        MessageType messageType = chattingMessage.getType();

        ChatMessageResponse response = ChatMessageResponse.builder()
            .roomId(chattingMessage.getRoomId())
            .messageType(messageType)
            .senderId(chattingMessage.getSenderId())
            .senderName(chattingMessage.getSenderName())
            .content(messageType == MessageType.MESSAGE ? chattingMessage.getContent() : null)
            .imageUrls(messageType == MessageType.IMAGE ? chattingMessage.getImageUrls() : null)
            .build();

        sendingOperations.convertAndSend(DEFAULT_TOPIC + roomId, response);
    }
}