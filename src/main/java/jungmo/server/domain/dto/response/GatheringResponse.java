package jungmo.server.domain.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
public class GatheringResponse {

    private Long id;
    private String title;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalTime startTime;
    private String memo;
    private List<UserResponse> gatheringUsers;
    private LocationResponse meetingLocation;
    private List<LocationResponse> Locations;


    @Builder
    public GatheringResponse(Long id, String title, LocalDate startDate, LocalDate endDate, LocalTime startTime, String memo, List<UserResponse> gatheringUsers, LocationResponse meetingLocation, List<LocationResponse> Locations) {
        this.id = id;
        this.title = title;
        this.startDate = startDate;
        this.endDate = endDate;
        this.startTime = startTime;
        this.memo = memo;
        this.gatheringUsers = gatheringUsers;
        this.meetingLocation = meetingLocation;
        this.Locations = Locations;
    }
}
