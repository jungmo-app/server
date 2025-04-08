package jungmo.server.domain.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
    private String message;
    private Long gatheringId;
    private String profileImage;
    private String title;
    private boolean isRead;

    private LocalDateTime createdAt = LocalDateTime.now();


    public Notification(User user, String message, Long gatheringId) {
        this.user = user;
        this.message = message;
        this.gatheringId = gatheringId;
        this.isRead = false;
    }

    public Notification(User user, String message, Long gatheringId, String profileImage, String title) {
        this.user = user;
        this.message = message;
        this.gatheringId = gatheringId;
        this.profileImage = profileImage;
        this.title = title;
        this.isRead = false;
    }
}
