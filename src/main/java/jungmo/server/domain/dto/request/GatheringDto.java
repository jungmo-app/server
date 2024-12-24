package jungmo.server.domain.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
public class GatheringDto {

    @NotBlank(message = "제목을 입력해주세요.")
    private String title;
    @NotNull(message = "시작날짜를 입력해주세요.")
    private LocalDate startDate;
    @NotNull(message = "종료날짜를 입력해주세요.")
    private LocalDate endDate;
    @NotNull(message = "시작시간을 입력해주세요.")
    private LocalTime startTime;
    private String memo;
    private List<Long> userIds;

    public GatheringDto(String title, LocalDate startDate, LocalDate endDate, LocalTime startTime, String memo, List<Long> userIds) {
        this.title = title;
        this.startDate = startDate;
        this.endDate = endDate;
        this.startTime = startTime;
        this.memo = memo;
        this.userIds = userIds;
    }
}
