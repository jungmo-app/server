package jungmo.server.domain.dto.response;

import jungmo.server.domain.dto.request.GatheringUserDto;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
public class GatheringResponseDto {

    private String title;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalTime startTime;
    private String memo;
    private List<UserDto> gatheringUsers;

    @Builder
    public GatheringResponseDto(String title, LocalDate startDate, LocalDate endDate, LocalTime startTime, String memo, List<UserDto> gatheringUsers) {
        this.title = title;
        this.startDate = startDate;
        this.endDate = endDate;
        this.startTime = startTime;
        this.memo = memo;
        this.gatheringUsers = gatheringUsers;
    }
}
