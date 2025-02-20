package jungmo.server.domain.repository;

import jungmo.server.domain.dto.response.GatheringListResponse;
import jungmo.server.domain.entity.Gathering;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GatheringRepository extends JpaRepository<Gathering, Long> {

    @Query("select new jungmo.server.domain.dto.response.GatheringListResponse(g.id,g.title,g.startDate,g.endDate,g.startTime) " +
            "from Gathering g " +
            "join g.gatheringUsers gu " +
            "join gu.user u " +
            "where u.id = :userId " +
            "and g.isDeleted = false " +
            "and (g.startDate <= CURRENT_DATE and g.endDate >= CURRENT_DATE)")
    List<GatheringListResponse> findAllByUserId(@Param("userId") Long userId);
}
