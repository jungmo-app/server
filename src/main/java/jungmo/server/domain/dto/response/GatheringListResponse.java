package jungmo.server.domain.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@NoArgsConstructor
public class GatheringListResponse {

    private Long id;
    private String title;
    private String profileImage;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate startDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate endDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
    private LocalTime startTime;
    private String meetingLocation;

    public GatheringListResponse(Long id, String title, String profileImage, LocalDate startDate, LocalDate endDate, LocalTime startTime, String meetingLocation) {
        this.id = id;
        this.title = title;
        this.profileImage = profileImage;
        this.startDate = startDate;
        this.endDate = endDate;
        this.startTime = startTime;
        this.meetingLocation = meetingLocation;
    }
}
