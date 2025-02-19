package jungmo.server.domain.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class NotificationRequest {

    private List<Long> notificationIds; // 읽음,삭제 처리할 알림 ID 리스트
}
