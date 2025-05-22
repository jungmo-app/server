package jungmo.server.domain.repository.chat;

import java.util.List;

public interface ChattingRoomRepositoryCustom {

    List<Long> findChattingRooIdsByUserId(Long memberId);

    List<Long> findChattingRoomUserIdsByChattingRoomId(Long chattingRoomId);
}
