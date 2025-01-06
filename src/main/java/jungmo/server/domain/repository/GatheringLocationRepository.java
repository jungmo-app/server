package jungmo.server.domain.repository;

import jungmo.server.domain.dto.response.LocationResponse;
import jungmo.server.domain.entity.GatheringLocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface GatheringLocationRepository extends JpaRepository<GatheringLocation, Long> {

    @Query("select new jungmo.server.domain.dto.response.LocationResponse(l.id,l.name,l.roadAddress,l.latitude,l.longitude,l.placeId,l.category,gl.isFirstLocation) " +
    "from GatheringLocation gl " +
            "join gl.location l " +
            "where gl.gathering.id = :gatheringId")
    List<LocationResponse> findAllByGatheringId(@Param("gatheringId") Long gatheringId);

    @Query("select gl " +
            "from GatheringLocation gl " +
            "where gl.gathering.id = :gatheringId and gl.isFirstLocation = true")
    Optional<GatheringLocation> findByFirstLocationAndGatheringId(@Param("gatheringId") Long gatheringId);
}