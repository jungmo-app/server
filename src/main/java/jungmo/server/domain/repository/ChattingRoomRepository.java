package jungmo.server.domain.repository;

import java.util.Optional;
import jungmo.server.domain.entity.ChattingRoom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChattingRoomRepository extends JpaRepository<ChattingRoom, Long> {

    Optional<ChattingRoom> findChattingRoomByGatheringId(Long gatheringId);
}
