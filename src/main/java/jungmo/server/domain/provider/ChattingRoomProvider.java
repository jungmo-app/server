package jungmo.server.domain.provider;

import jungmo.server.domain.entity.ChattingRoom;
import jungmo.server.domain.repository.ChattingRoomRepository;
import jungmo.server.global.error.ErrorCode;
import jungmo.server.global.error.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ChattingRoomProvider {

    private final ChattingRoomRepository chattingRoomRepository;

    public ChattingRoom findChattingRoom(Long gatheringId) {

        ChattingRoom chattingRoom = chattingRoomRepository.findChattingRoomByGatheringId(gatheringId)
            .orElseThrow(() -> new BusinessException(ErrorCode.CHATTING_ROOM_NOT_EXISTS));
        if (chattingRoom.getIsDel()) {
            throw new BusinessException(ErrorCode.CHATTING_ROOM_ALREADY_DELETED);
        }

        return chattingRoom;
    }
}
