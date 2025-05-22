package jungmo.server.domain.repository.chat;

import java.util.Optional;
import jungmo.server.domain.entity.ChattingRoom;
import jungmo.server.domain.entity.Gathering;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChattingRoomRepository extends JpaRepository<ChattingRoom, Long>, ChattingRoomRepositoryCustom {

    Optional<ChattingRoom> findChattingRoomByGatheringId(Long gatheringId);

    boolean existsChattingRoomByGatheringId(Long gatheringId);
}
