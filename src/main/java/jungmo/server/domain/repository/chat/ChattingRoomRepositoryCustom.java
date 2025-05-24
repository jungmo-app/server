package jungmo.server.domain.repository.chat;

import java.util.List;
import jungmo.server.domain.entity.User;

public interface ChattingRoomRepositoryCustom {

    List<Long> findChattingRooIdsByUserId(Long memberId);

    List<User> findChattingRoomUserIdsByChattingRoomId(Long chattingRoomId);
}
