package jungmo.server.domain.dto.response;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class GatheringListResponseDto {

    private String title;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalTime startTime;

    public GatheringListResponseDto(String title, LocalDate startDate, LocalDate endDate, LocalTime startTime) {
        this.title = title;
        this.startDate = startDate;
        this.endDate = endDate;
        this.startTime = startTime;
    }
}
