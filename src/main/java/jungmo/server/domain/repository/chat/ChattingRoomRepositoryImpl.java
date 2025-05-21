package jungmo.server.domain.repository.chat;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import java.util.List;
import jungmo.server.domain.entity.QChattingRoom;
import jungmo.server.domain.entity.QGathering;
import jungmo.server.domain.entity.QGatheringUser;
import org.springframework.stereotype.Repository;

@Repository
public class ChattingRoomRepositoryImpl implements ChattingRoomRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    public ChattingRoomRepositoryImpl(EntityManager em) {
        this.jpaQueryFactory = new JPAQueryFactory(em);
    }

    @Override
    public List<Long> findChattingRooIdsByUserId(Long memberId) {

        QChattingRoom cr = QChattingRoom.chattingRoom;
        QGathering gr = QGathering.gathering;
        QGatheringUser gu = QGatheringUser.gatheringUser;

        return jpaQueryFactory
            .select(cr.id)
            .from(cr)
            .join(gr).on(gr.id.eq(cr.gathering.id))
            .join(gu).on(gu.gathering.id.eq(gr.id))
            .where(
                gu.user.id.eq(memberId),
                gr.isDeleted.isFalse(),
                cr.isDel.isFalse()
            )
            .fetch();
    }
}
