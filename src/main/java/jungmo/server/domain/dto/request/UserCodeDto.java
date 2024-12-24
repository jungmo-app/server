package jungmo.server.domain.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserCodeDto {

    @NotBlank
    private String userCode;

    public UserCodeDto(String userCode) {
        this.userCode = userCode;
    }
}
