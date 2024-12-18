package jungmo.server.domain.repository;

import jungmo.server.domain.dto.response.GatheringListResponseDto;
import jungmo.server.domain.entity.Gathering;
import jungmo.server.domain.entity.GatheringStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface GatheringRepository extends JpaRepository<Gathering, Long> {

    @Query("select new jungmo.server.domain.dto.response.GatheringListResponseDto(g.title,g.startDate,g.endDate,g.startTime) " +
    "from Gathering g " +
    "join g.gatheringUsers gu " +
    "where gu.user.id = :userId and gu.status = :status")
    List<GatheringListResponseDto> findAllByUserId(@Param("userId") Long userId, @Param("status") GatheringStatus status);
}
