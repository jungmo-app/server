package jungmo.server.domain.service.chat;

import java.util.List;
import jungmo.server.domain.dto.request.chat.ChattingMessageRequest;
import jungmo.server.domain.dto.response.chat.ChatMessageResponse;
import jungmo.server.domain.entity.User;
import jungmo.server.domain.handler.chat.ChatMessageHandler;
import jungmo.server.domain.provider.UserDataProvider;
import jungmo.server.domain.repository.chat.ChattingMessageRepository;
import jungmo.server.global.error.ErrorCode;
import jungmo.server.global.error.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatMessageService {

    private final UserDataProvider userDataProvider;
    private final List<ChatMessageHandler> handlerList;

    private final ChattingMessageRepository chattingMessageRepository;

    @Transactional
    public void processChatMessages(ChattingMessageRequest request, Long roomId) {

//        User user = userDataProvider.getUser();
//        log.info("현재 로그인 유저 Email : {}", user.getEmail());

        User user = null;

        ChatMessageHandler handler = handlerList.stream()
            .filter(h -> h.getMessageType().equals(request.getMessageType()))
            .findFirst()
            .orElseThrow(() -> new BusinessException(ErrorCode.INVALID_CHATTING_MESSAGE_TYPE));

        handler.handleMessage(request, roomId, user);
    }

    @Transactional(readOnly = true)
    public Page<ChatMessageResponse> getChatMessagesByRoomId(Long roomId, Pageable pageable) {

        return chattingMessageRepository.findChattingMessagesByRoomIdWithPagingAndFiltering(pageable, roomId);
    }
}
