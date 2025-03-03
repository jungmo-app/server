package jungmo.server.domain.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jungmo.server.domain.dto.response.GatheringListResponse;
import jungmo.server.domain.entity.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public class GatheringRepositoryImpl implements GatheringRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    public GatheringRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public List<GatheringListResponse> findAllByUserIdAndDate(Long userId, LocalDate currentDate) {

        QGathering g = QGathering.gathering;
        QGatheringUser gu = QGatheringUser.gatheringUser;
        QGatheringLocation gl = QGatheringLocation.gatheringLocation;

        QGatheringUser creator = new QGatheringUser("creator");
        QUser creatorUser = new QUser("creatorUser");

        return queryFactory
                .select(Projections.constructor(GatheringListResponse.class,
                        g.id,
                        g.title,
                        creatorUser.profileImage,  // 추가된 프로필 이미지 필드
                        g.startDate,
                        g.endDate,
                        g.startTime,
                        gl.location.placeId
                ))
                .from(g)
                .join(g.gatheringUsers, gu)
                .join(g.gatheringUsers, creator).on(creator.authority.eq(Authority.WRITE))
                .join(creator.user, creatorUser)
                .join(g.gatheringLocations, gl)
                .where(
                        gu.user.id.eq(userId),
                        gl.isFirstLocation.isTrue(),
                        g.isDeleted.isFalse(),
                        g.startDate.loe(currentDate),
                        g.endDate.goe(currentDate)
                )
                .fetch();
    }
}
