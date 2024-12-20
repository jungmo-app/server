package jungmo.server.domain.dto.response;

import lombok.Data;

@Data
public class UserRegisterDto {
    private Long userId;
    private String userCode;

    public UserRegisterDto(Long userId, String userCode) {
        this.userId = userId;
        this.userCode = userCode;
    }
}
