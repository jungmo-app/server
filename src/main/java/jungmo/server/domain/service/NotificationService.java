package jungmo.server.domain.service;


import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jungmo.server.domain.dto.response.NotificationResponse;
import jungmo.server.domain.entity.Notification;
import jungmo.server.domain.entity.User;
import jungmo.server.domain.provider.UserDataProvider;
import jungmo.server.domain.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final UserDataProvider userDataProvider;

    @PersistenceContext
    EntityManager entityManager;
    private final Map<Long, List<SseEmitter>> emitters = new ConcurrentHashMap<>();

    /**
     * 알림 db에 저장
     * @param userId 알림 수신자 id
     * @param message
     * @return
     */

    @Transactional
    public Notification saveNotification(Long userId, Long gatheringId, String message) {
        Notification notification = new Notification();
        User user = userDataProvider.findUserById(userId);
        notification.setUser(user);
        notification.setMessage(message);
        notification.setGatheringId(gatheringId);
        return notificationRepository.save(notification);
    }

    @Transactional
    public List<NotificationResponse> getNotifications() {
        List<Notification> notifications =  notificationRepository.findByUserId(userDataProvider.getUser().getId());
        return notifications.stream()
                .map(NotificationResponse::from).collect(Collectors.toList());
    }

    @Transactional
    public void markAsRead(List<Long> notificationIds) {
        if (notificationIds != null && !notificationIds.isEmpty()) {
            notificationRepository.markAsRead(notificationIds);
        }
        entityManager.flush();
    }
}