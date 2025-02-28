package jungmo.server.domain.repository;

import jungmo.server.domain.dto.response.GatheringListResponse;
import jungmo.server.domain.entity.Gathering;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface GatheringRepository extends JpaRepository<Gathering, Long> {

    @Query("select new jungmo.server.domain.dto.response.GatheringListResponse(g.id,g.title,g.startDate,g.endDate,g.startTime, gl.location.placeId) " +
            "from Gathering g " +
            "join g.gatheringUsers gu " +
            "join g.gatheringLocations gl " +
            "join gu.user u " +
            "where u.id = :userId " +
            "and gl.isFirstLocation = true " +
            "and g.isDeleted = false " +
            "and (g.startDate <= :currentDate and g.endDate >= :currentDate)")
    List<GatheringListResponse> findAllByUserIdaAndDate(@Param("userId") Long userId, @Param("currentDate") LocalDate currentDate);
}
