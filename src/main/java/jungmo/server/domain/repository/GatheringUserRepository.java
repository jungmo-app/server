package jungmo.server.domain.repository;

import jungmo.server.domain.dto.response.GatheringListResponseDto;
import jungmo.server.domain.entity.Authority;
import jungmo.server.domain.entity.Gathering;
import jungmo.server.domain.entity.GatheringUser;
import jungmo.server.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface GatheringUserRepository extends JpaRepository<GatheringUser,Long> {

    @Query("select gu " +
    "from GatheringUser gu " +
    "join fetch gu.user u " +
    "join fetch gu.gathering g " +
    "where u = :user and g = :gathering and gu.authority = :authority and g.isDeleted = false")
    Optional<GatheringUser> findByAuthority(@Param("user") User user, @Param("gathering") Gathering gathering, @Param("authority") Authority authority);

    Optional<GatheringUser> findGatheringUserByUserIdAndGatheringId(@Param("user_id") Long userId, @Param("gathering_id") Long gatheringId);
}
