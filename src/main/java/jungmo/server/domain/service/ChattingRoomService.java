package jungmo.server.domain.service;

import jungmo.server.domain.entity.ChattingRoom;
import jungmo.server.domain.entity.Gathering;
import jungmo.server.domain.provider.ChattingRoomProvider;
import jungmo.server.domain.repository.ChattingRoomRepository;
import jungmo.server.global.aop.annotation.CheckWritePermission;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChattingRoomService {

    private final ChattingRoomRepository chattingRoomRepository;
    private final ChattingRoomProvider chattingRoomProvider;

    @CheckWritePermission
    public ChattingRoom saveChattingRoom(Gathering gathering) {

        return chattingRoomRepository.save(ChattingRoom.create(gathering));
    }

    @CheckWritePermission
    public void deleteChattingRoom(Long gatheringId) {

        ChattingRoom chattingRoom = chattingRoomProvider.findChattingRoom(gatheringId);

        chattingRoom.setIsDel(true);
    }
}
