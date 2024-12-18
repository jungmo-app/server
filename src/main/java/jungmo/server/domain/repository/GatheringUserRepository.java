package jungmo.server.domain.repository;

import jungmo.server.domain.entity.GatheringUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GatheringUserRepository extends JpaRepository<GatheringUser,Long> {
}
