package jungmo.server.domain.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserCodeRequest {

    @NotBlank
    private String userCode;

    public UserCodeRequest(String userCode) {
        this.userCode = userCode;
    }
}
