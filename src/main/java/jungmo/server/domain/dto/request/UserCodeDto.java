package jungmo.server.domain.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserCodeDto {

    @NotBlank
    private String userCode;

    public UserCodeDto(String userCode) {
        this.userCode = userCode;
    }
}
