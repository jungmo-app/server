package jungmo.server.domain.event;

import jungmo.server.domain.dto.response.EventPayload;
import jungmo.server.domain.entity.Gathering;
import jungmo.server.domain.entity.Notification;
import jungmo.server.domain.provider.GatheringDataProvider;
import jungmo.server.domain.provider.GatheringLocationDataProvider;
import jungmo.server.domain.repository.GatheringRepository;
import jungmo.server.domain.repository.NotificationRepository;
import jungmo.server.domain.service.SseEmitterService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionalEventListener;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationEventListener {

    private final NotificationRepository notificationRepository;
    private final SseEmitterService sseEmitterService;
    private final Executor notificationTaskExecutor;

    @Async
    @TransactionalEventListener
    public void sendNotificationsAfterTransaction(NotificationEvent event) {
        log.info("✅ 트랜잭션 종료 후 알림 전송 시작: {}", event.getNotificationIds());

        List<Notification> notifications = notificationRepository.findAllById(event.getNotificationIds());
        if (!notifications.isEmpty()) {
            notifications.forEach(notification -> {
                CompletableFuture.runAsync(() -> sendWithMultiThreading(notification, event.getStartDate()), notificationTaskExecutor);
            });
            log.info("✅ 트랜잭션 종료 후 알림 전송 완료!");
        }
    }

    private void sendWithMultiThreading(Notification notification,String startDate) {
        try {
            sseEmitterService.sendToClient(
                    notification.getUser().getId(),
                    EventPayload.builder()
                            .userId(notification.getUser().getId())
                            .gatheringId(notification.getGatheringId())
                            .startDate(startDate)
                            .createdAt(notification.getCreatedAt().toString())
                            .isRead(notification.isRead())
                            .message(notification.getMessage())
                            .build());
        } catch (Exception e) {
            log.error("❌ SSE 알림 전송 실패 : {}", e.getMessage(), e);
        }
    }
}
