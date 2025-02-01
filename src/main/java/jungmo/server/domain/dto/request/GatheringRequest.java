package jungmo.server.domain.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
public class GatheringRequest {

    @NotBlank(message = "제목을 입력해주세요.")
    private String title;
    @NotNull(message = "시작날짜를 입력해주세요.")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate startDate;
    @NotNull(message = "종료날짜를 입력해주세요.")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate endDate;
    @NotNull(message = "시작시간을 입력해주세요.")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
    @Schema(description = "모임 시작 시간 (HH:mm)", example = "14:30")
    private LocalTime startTime;
    private LocationRequest meetingLocation;
    private String memo;
    private List<Long> userIds;

    public GatheringRequest(String title, LocalDate startDate, LocalDate endDate, LocalTime startTime, LocationRequest meetingLocation, String memo, List<Long> userIds) {
        this.title = title;
        this.startDate = startDate;
        this.endDate = endDate;
        this.startTime = startTime;
        this.meetingLocation = meetingLocation;
        this.memo = memo;
        this.userIds = userIds;
    }
}
