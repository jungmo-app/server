package jungmo.server.domain.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PositionRequest {

    @Min(value = -90, message = "위도(latitude)는 -90 이상이어야 합니다.")
    @Max(value = 90, message = "위도(latitude)는 90 이하이어야 합니다.")
    private Double latitude;

    @Min(value = -180, message = "경도(longitude)는 -180 이상이어야 합니다.")
    @Max(value = 180, message = "경도(longitude)는 180 이하이어야 합니다.")
    private Double longitude;
}
