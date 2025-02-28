package jungmo.server.domain.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class LocationRequest {

    @NotBlank(message = "장소의 고유ID를 입력해주세요.")
    @Size(max = 50, message = "장소의 고유ID는 최대 50자까지 가능합니다.")
    private String placeId;

}
