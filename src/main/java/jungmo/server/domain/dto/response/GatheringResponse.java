package jungmo.server.domain.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import jungmo.server.domain.entity.Authority;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
@NoArgsConstructor
public class GatheringResponse {

    private Authority authority;
    private Long id;
    private String title;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate startDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate endDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
    private LocalTime startTime;
    private String memo;
    private List<UserResponse> gatheringUsers;
    private LocationResponse meetingLocation;
    private List<LocationResponse> Locations;


    @Builder
    public GatheringResponse(Authority authority, Long id, String title, LocalDate startDate, LocalDate endDate, LocalTime startTime, String memo, List<UserResponse> gatheringUsers, LocationResponse meetingLocation, List<LocationResponse> Locations) {
        this.authority = authority;
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

    public void setAuthority(Authority authority) {
        this.authority = authority;
    }
}
