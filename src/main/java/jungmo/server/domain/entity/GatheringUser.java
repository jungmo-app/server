package jungmo.server.domain.entity;

import jakarta.persistence.*;
import jungmo.server.global.auditing.BaseTimeEntity;
import lombok.*;

@Getter
@Entity
@Table(name = "gathering_user")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GatheringUser extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "gathering_user_id")
    private Long id;
    @Enumerated(value = EnumType.STRING)
    private Authority authority;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gathering_id")
    private Gathering gathering;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Builder
    public GatheringUser(Long id, Authority authority, Gathering gathering, User user) {
        this.id = id;
        this.authority = authority;
        this.gathering = gathering;
        this.user = user;
    }

    /**
     * 연관관계 편의메서드
     */
    public GatheringUser setUser(User user) {
        this.user = user;
        user.getGatheringUserList().add(this);
        return this;
    }

    public void removeUser(User user) {
        if (this.user != null) {
            this.user.getGatheringUserList().remove(this);
            this.user = null;
        }
    }

    public GatheringUser setGathering(Gathering gathering) {
        this.gathering = gathering;
        gathering.getGatheringUsers().add(this);
        return this;
    }

    public void removeGathering(Gathering gathering) {
        if (this.gathering != null) {
            this.gathering.getGatheringUsers().remove(this);
            this.gathering = null;
        }
    }
}
