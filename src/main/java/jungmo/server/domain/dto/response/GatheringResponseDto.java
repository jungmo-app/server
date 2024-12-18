package jungmo.server.domain.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class GatheringResponseDto {

    private String title;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalTime startTime;
    private String memo;
    private Boolean isConnected;

    @Builder
    public GatheringResponseDto(String title, LocalDate startDate, LocalDate endDate, LocalTime startTime, String memo, Boolean isConnected) {
        this.title = title;
        this.startDate = startDate;
        this.endDate = endDate;
        this.startTime = startTime;
        this.memo = memo;
        this.isConnected = isConnected;
    }
}
