package jungmo.server.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChattingRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gathering_id", nullable = false)
    private Gathering gathering;

    @Column(nullable = false)
    private LocalDate createdAt;

    @Setter
    @Column(nullable = false)
    private Boolean isDel;

    private ChattingRoom(Gathering gathering) {
        this.gathering = gathering;
        this.createdAt = LocalDate.now();
        this.isDel = false;
    }

    public static ChattingRoom create(Gathering gathering) {
        return new ChattingRoom(gathering);
    }
}
