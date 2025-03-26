package jungmo.server.domain.dto.response;

import jungmo.server.domain.entity.Notification;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotificationResponse {

    private Long notificationId;
    private Long userId;
    private String message;
    private Long gatheringId;
    private String title;
    private String profileImage;
    private String createdAt;
    private boolean isRead;

    public static NotificationResponse from(Notification notification) {
        NotificationResponse dto = new NotificationResponse();
        dto.setNotificationId(notification.getId());
        dto.setUserId(notification.getUser().getId());
        dto.setMessage(notification.getMessage());
        dto.setGatheringId(notification.getGatheringId());
        dto.setTitle(notification.getTitle());
        dto.setProfileImage(notification.getProfileImage());
        dto.setRead(notification.isRead());
        dto.setCreatedAt(notification.getCreatedAt().toString());
        return dto;
    }
}
