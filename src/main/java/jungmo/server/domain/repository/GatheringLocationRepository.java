package jungmo.server.domain.repository;

import jungmo.server.domain.dto.response.LocationResponseDto;
import jungmo.server.domain.entity.GatheringLocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface GatheringLocationRepository extends JpaRepository<GatheringLocation, Long> {

    @Query("select new jungmo.server.domain.dto.response.LocationResponseDto(l.id,l.name,l.roadAddress,l.latitude,l.longitude,l.placeId,l.category,gl.isFirstLocation) " +
    "from GatheringLocation gl " +
            "join gl.location l " +
            "where gl.gathering.id = :gatheringId")
    List<LocationResponseDto> findAllByGatheringId(@Param("gatheringId") Long gatheringId);
}
