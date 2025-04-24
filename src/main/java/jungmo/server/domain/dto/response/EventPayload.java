package jungmo.server.domain.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.Data;

@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class EventPayload {

    private Long notificationId;
    private String title;
    private String profileImage;
    private Long gatheringId;
    private String startDate;
    private String createdAt;
    private String message;
    private boolean isRead;

}