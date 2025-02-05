package jungmo.server.domain.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class LocationRequest {
    @NotBlank(message = "장소의 이름을 입력해주세요.")
    @Size(max = 100, message = "장소의 이름은 최대 100자까지 가능합니다.")
    private String name;
    @NotBlank(message = "장소의 도로명주소를 입력해주세요.")
    @Size(max = 200, message = "도로명주소는 최대 200자까지 가능합니다.")
    private String roadAddress;
    @NotNull(message = "장소의 위도를 입력해주세요.")
    @DecimalMin(value = "-90.0", message = "위도는 -90 이상이어야 합니다.")
    @DecimalMax(value = "90.0", message = "위도는 90 이하여야 합니다.")
    private double latitude;
    @NotNull(message = "장소의 경도를 입력해주세요.")
    @DecimalMin(value = "-180.0", message = "경도는 -180 이상이어야 합니다.")
    @DecimalMax(value = "180.0", message = "경도는 180 이하여야 합니다.")
    private double longitude;
    @NotBlank(message = "장소의 카테고리를 입력해주세요.")
    @Size(max = 50, message = "카테고리는 최대 50자까지 가능합니다.")
    private String category;
}
