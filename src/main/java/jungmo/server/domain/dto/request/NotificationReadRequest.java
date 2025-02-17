package jungmo.server.domain.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class NotificationReadRequest {
    private List<Long> notificationIds; // 읽음 처리할 알림 ID 리스트
}
