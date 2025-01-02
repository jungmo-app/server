package jungmo.server.domain.dto.response;

import jungmo.server.domain.dto.request.GatheringUserDto;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
public class GatheringResponseDto {

    private Long id;
    private String title;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalTime startTime;
    private String memo;
    private List<UserDto> gatheringUsers;
    private LocationResponseDto meetingPlace;
    private List<LocationResponseDto> places;


    @Builder
    public GatheringResponseDto(Long id, String title, LocalDate startDate, LocalDate endDate, LocalTime startTime, String memo, List<UserDto> gatheringUsers, LocationResponseDto meetingPlace, List<LocationResponseDto> places) {
        this.id = id;
        this.title = title;
        this.startDate = startDate;
        this.endDate = endDate;
        this.startTime = startTime;
        this.memo = memo;
        this.gatheringUsers = gatheringUsers;
        this.meetingPlace = meetingPlace;
        this.places = places;
    }
}
