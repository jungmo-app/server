package jungmo.server.domain.repository;

import jungmo.server.domain.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByUserId(Long userId);

    @Modifying
    @Query("UPDATE Notification n SET n.isRead = true WHERE n.id IN :notificationIds")
    void markAsRead(@Param("notificationIds") List<Long> notificationIds);

}
