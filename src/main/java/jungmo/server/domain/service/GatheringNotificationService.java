package jungmo.server.domain.service;

import jungmo.server.domain.entity.*;
import jungmo.server.domain.event.NotificationEvent;
import jungmo.server.domain.provider.GatheringDataProvider;
import jungmo.server.domain.provider.UserDataProvider;
import jungmo.server.domain.repository.GatheringUserRepository;
import jungmo.server.domain.repository.NotificationRepository;
import jungmo.server.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
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
    private final GatheringUserRepository gatheringUserRepository;



    public void invite(Set<Long> userIds, Long gatheringId) {
        Gathering gathering = gatheringDataProvider.findGathering(gatheringId);
        Optional<GatheringUser> writeUser = gatheringUserRepository.findByAuthorityAndGathering(gathering, Authority.WRITE);
        String profileImage = writeUser.get().getUser().getProfileImage();
        List<User> users = userRepository.findAllById(userIds);
        String message = gathering.getTitle() + " 모임에 초대되었습니다.";
        String sse = "invite";
        LocalDate startDate = gathering.getStartDate();
        sendBulkNotification(users, gatheringId, message, profileImage, gathering.getTitle(), String.valueOf(startDate),sse);
    }


    public void update(Set<Long> remainingUsers, Long gatheringId) {
        Gathering gathering = gatheringDataProvider.findGathering(gatheringId);
        Optional<GatheringUser> writeUser = gatheringUserRepository.findByAuthorityAndGathering(gathering, Authority.WRITE);
        String profileImage = writeUser.get().getUser().getProfileImage();
        List<User> users = userRepository.findAllById(remainingUsers);
        String message = gathering.getTitle() + " 모임이 수정되었습니다.";
        String sse = "update";
        LocalDate startDate = gathering.getStartDate();
        sendBulkNotification(users, gatheringId, message, profileImage, gathering.getTitle(), String.valueOf(startDate),sse);
    }

    public void remove(Set<Long> remainingUsers, Long gatheringId) {
        Gathering gathering = gatheringDataProvider.findGathering(gatheringId);
        Optional<GatheringUser> writeUser = gatheringUserRepository.findByAuthorityAndGathering(gathering, Authority.WRITE);
        String profileImage = writeUser.get().getUser().getProfileImage();
        List<User> users = userRepository.findAllById(remainingUsers);
        String message = gathering.getTitle() + " 모임에서 퇴출되었습니다.";
        String sse = "remove";
        LocalDate startDate = gathering.getStartDate();
        sendBulkNotification(users, gatheringId, message, profileImage, gathering.getTitle(), String.valueOf(startDate),sse);
    }

    public void delete(Set<Long> existingUsers, Long gatheringId) {
        Gathering gathering = gatheringDataProvider.findGathering(gatheringId);
        Optional<GatheringUser> writeUser = gatheringUserRepository.findByAuthorityAndGathering(gathering, Authority.WRITE);
        String profileImage = writeUser.get().getUser().getProfileImage();
        List<User> users = userRepository.findAllById(existingUsers);
        String message = gathering.getTitle() + " 모임이 삭제되었습니다.";
        String sse = "delete";
        LocalDate startDate = gathering.getStartDate();
        sendBulkNotification(users, gatheringId, message, profileImage, gathering.getTitle(), String.valueOf(startDate),sse);
    }

    public void sendBulkNotification(List<User> users, Long gatheringId, String message, String profileImage, String title, String startDate, String sse) {
        List<Notification> notifications = users.stream()
                .map(user -> new Notification(user, message, gatheringId,profileImage,title))
                .collect(Collectors.toList());

        List<Long> notificationIds = notificationRepository.saveAll(notifications).stream()
                .map(Notification::getId).collect(Collectors.toList());

        eventPublisher.publishEvent(new NotificationEvent(notificationIds,startDate,sse));
    }
}
