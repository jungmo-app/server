package jungmo.server.domain.repository;

import jungmo.server.domain.entity.Location;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LocationRepository extends JpaRepository<Location, Long> {

    Optional<Location> findByRoadAddress(String roadAddress);
}
