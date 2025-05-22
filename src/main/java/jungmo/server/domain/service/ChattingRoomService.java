package jungmo.server.domain.service;

import java.util.List;
import jungmo.server.domain.dto.response.chat.UserChattingRoomsResponse;
import jungmo.server.domain.entity.ChattingRoom;
import jungmo.server.domain.entity.Gathering;
import jungmo.server.domain.entity.User;
import jungmo.server.domain.provider.ChattingRoomProvider;
import jungmo.server.domain.provider.UserDataProvider;
import jungmo.server.domain.repository.chat.ChattingRoomRepository;
import jungmo.server.global.aop.annotation.CheckWritePermission;
import jungmo.server.global.error.ErrorCode;
import jungmo.server.global.error.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChattingRoomService {

    private final ChattingRoomRepository chattingRoomRepository;
    private final ChattingRoomProvider chattingRoomProvider;

    private final UserDataProvider userDataProvider;

    @CheckWritePermission
    public ChattingRoom saveChattingRoom(Gathering gathering) {

        // 이미 존재하는 채팅방인 지 체크
        if (chattingRoomRepository.existsChattingRoomByGatheringId(gathering.getId())) {
            throw new BusinessException(ErrorCode.CHATTING_ROOM_ALREADY_EXISTS);
        }

        return chattingRoomRepository.save(ChattingRoom.create(gathering));
    }

    @CheckWritePermission
    public void deleteChattingRoom(Long gatheringId) {

        ChattingRoom chattingRoom = chattingRoomProvider.findChattingRoom(gatheringId);

        chattingRoom.setIsDel(true);
    }

    @Transactional(readOnly = true)
    public UserChattingRoomsResponse getChattingRoomIdsByUserId() {

        User user = userDataProvider.getUser();

        List<Long> chatRoomIds = chattingRoomRepository.findChattingRooIdsByUserId(user.getId());

        return new UserChattingRoomsResponse(user.getId(), chatRoomIds);
    }

    @Transactional(readOnly = true)
    public List<Long> getChattingRoomUserByChattingRoomId(Long chattingRoomId) {

        return chattingRoomRepository.findChattingRoomUserIdsByChattingRoomId(chattingRoomId);
    }
}
