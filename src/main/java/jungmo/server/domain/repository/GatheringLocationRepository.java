package jungmo.server.domain.repository;

import jungmo.server.domain.entity.GatheringLocation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GatheringLocationRepository extends JpaRepository<GatheringLocation, Long> {

}
