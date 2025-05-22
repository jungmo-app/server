package jungmo.server.domain.repository.chat;

import jungmo.server.domain.dto.response.chat.ChatMessageResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ChattingMessageRepositoryCustom {
    Page<ChatMessageResponse> findChattingMessagesByRoomIdWithPagingAndFiltering(Pageable page, Long roomId);

}
