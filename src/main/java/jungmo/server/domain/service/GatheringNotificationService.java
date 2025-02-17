package jungmo.server.domain.service;

import jungmo.server.domain.entity.Gathering;
import jungmo.server.domain.entity.Notification;
import jungmo.server.domain.entity.User;
import jungmo.server.domain.event.NotificationEvent;
import jungmo.server.domain.provider.GatheringDataProvider;
import jungmo.server.domain.provider.UserDataProvider;
import jungmo.server.domain.repository.NotificationRepository;
import jungmo.server.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class GatheringNotificationService {

    private final ApplicationEventPublisher eventPublisher;
    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final GatheringDataProvider gatheringDataProvider;



    public void invite(Set<Long> userIds, Long gatheringId) {
        Gathering gathering = gatheringDataProvider.findGathering(gatheringId);
        List<User> users = userRepository.findAllById(userIds);
        String message = gathering.getTitle() + " 모임에 초대되었습니다.";
        sendBulkNotification(users,gatheringId,message);
    }

    public void sendBulkNotification(List<User> users, Long teamId, String message) {
        List<Notification> notifications = users.stream()
                .map(user -> new Notification(user, message, teamId))
                .collect(Collectors.toList());

        List<Long> notificationIds = notificationRepository.saveAll(notifications).stream()
                .map(Notification::getId).collect(Collectors.toList());

        eventPublisher.publishEvent(new NotificationEvent(notificationIds));
    }
}
