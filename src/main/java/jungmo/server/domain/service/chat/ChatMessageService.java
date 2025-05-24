package jungmo.server.domain.service.chat;

import java.security.Principal;
import java.util.List;
import java.util.Objects;
import jungmo.server.domain.dto.request.chat.ChattingMessageRequest;
import jungmo.server.domain.dto.response.chat.ChatMessageResponse;
import jungmo.server.domain.entity.User;
import jungmo.server.domain.entity.chat.ChattingMessage;
import jungmo.server.domain.provider.UserDataProvider;
import jungmo.server.domain.repository.chat.ChattingMessageRepository;
import jungmo.server.domain.service.ChattingRoomService;
import jungmo.server.global.auth.service.PrincipalDetails;
import jungmo.server.global.error.ErrorCode;
import jungmo.server.global.error.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatMessageService {

    private final UserDataProvider userDataProvider;

    private final ChattingMessageRepository chattingMessageRepository;

    private final ChatMessageHandleService chatMessageHandleService;
    private final ChattingRoomService chattingRoomService;

    @Transactional
    public void processChatMessages(ChattingMessageRequest request, Long roomId, Principal principal) {

        // 1. 채팅방에 참여하고 있는 회원들 조회
        List<User> users = chattingRoomService.getChattingRoomUserByChattingRoomId(roomId);
        if (Objects.isNull(users) || users.isEmpty()) {
            throw new BusinessException(ErrorCode.CHATTING_ROOM_MEMBERS_NOT_EXISTS);
        }

        // ( 온라인, 오프라인 여부와 채팅방 참여중인지 여부를 체크 )

        // 2. NoSQL DB에 메시지 저장
        ChattingMessage message = null;
        if (principal instanceof Authentication auth &&
        auth.getPrincipal() instanceof PrincipalDetails principalDetails) {
            User user = principalDetails.getUser();
            message = chatMessageHandleService.saveMessage(request, roomId, user);
        } else {
            throw new BusinessException(ErrorCode.INVALID_AUTH_PRINCIPAL);
        }

        // 3. 온라인 회원들에게 메시지 전송
        chatMessageHandleService.sendMessage(message, roomId);
    }

    @Transactional(readOnly = true)
    public Page<ChatMessageResponse> getChatMessagesByRoomId(Long roomId, Pageable pageable) {

        return chattingMessageRepository.findChattingMessagesByRoomIdWithPagingAndFiltering(pageable, roomId);
    }
}
