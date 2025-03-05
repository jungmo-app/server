package jungmo.server.domain.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jungmo.server.domain.entity.QGatheringLocation;
import jungmo.server.domain.entity.QLocation;
import org.springframework.stereotype.Repository;

@Repository
public class GatheringLocationRepositoryImpl implements GatheringLocationCustum{

    private final JPAQueryFactory queryFactory;

    public GatheringLocationRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public boolean isPlaceAlreadyExists(Long gatheringId, String placeId) {

        QGatheringLocation gl = QGatheringLocation.gatheringLocation;
        QLocation l = QLocation.location;

        return queryFactory
                .selectOne()
                .from(gl)
                .join(gl.location, l)
                .where(
                        gl.gathering.id.eq(gatheringId),
                        l.placeId.eq(placeId)
                )
                .fetchFirst() != null; // 데이터가 존재하면 true 반환

    }
}
