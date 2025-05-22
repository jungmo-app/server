package jungmo.server.domain.service.chat;

import jungmo.server.domain.dto.request.chat.ChattingMessageRequest;
import jungmo.server.domain.entity.User;
import jungmo.server.domain.entity.chat.ChattingMessage;
import jungmo.server.domain.repository.chat.ChattingMessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ChatMessageSaveService {

    private final ChattingMessageRepository chattingMessageRepository;

    @Transactional
    public void saveTextMessage(
        ChattingMessageRequest chattingMessageRequest, Long roomId, User user) {

        ChattingMessage chattingMessage = ChattingMessage.create(
            chattingMessageRequest.getMessageType(),
            roomId,
            10L,
            "tester",
            chattingMessageRequest.getMessage(),
            null,
            chattingMessageRequest.getSendTime()
        );

        // TODO : 이미 저장했던 메시지 체크 해 줄 필요가 있을까? 있다면 어떻게 해야할까 ?

        chattingMessageRepository.save(chattingMessage);
    }

    @Transactional
    public void saveImageMessage(ChattingMessageRequest chattingMessageRequest, Long roomId, User user) {

        ChattingMessage chattingMessage = ChattingMessage.create(
            chattingMessageRequest.getMessageType(),
            roomId,
            10L,
            "tester",
            null,
            chattingMessageRequest.getImageUrls(),
            chattingMessageRequest.getSendTime()
        );

        chattingMessageRepository.save(chattingMessage);
    }
}
