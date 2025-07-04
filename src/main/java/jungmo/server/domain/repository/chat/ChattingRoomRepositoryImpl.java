package jungmo.server.domain.repository.chat;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import java.util.List;
import jungmo.server.domain.entity.QChattingRoom;
import jungmo.server.domain.entity.QGathering;
import jungmo.server.domain.entity.QGatheringUser;
import jungmo.server.domain.entity.User;
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
            .join(gr)
            .join(gu)
            .where(
                gu.user.id.eq(memberId),
                gr.isDeleted.isFalse(),
                cr.isDel.isFalse()
            )
            .fetch();
    }

    @Override
    public List<User> findChattingRoomUserIdsByChattingRoomId(Long chattingRoomId) {

        QChattingRoom cr = QChattingRoom.chattingRoom;
        QGathering gr = QGathering.gathering;
        QGatheringUser gu = QGatheringUser.gatheringUser;

        return jpaQueryFactory
            .select(gu.user)
            .from(cr)
            .join(gr)
            .join(gu)
            .where(
                cr.id.eq(chattingRoomId),
                gr.isDeleted.isFalse(),
                cr.isDel.isFalse()
            )
            .fetch();
    }
}
