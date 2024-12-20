package jungmo.server.domain.dto.response;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserDto {

    private Long id;
    private String userCode;
    private String userName;
    private String profileImage;

    public UserDto(Long id, String userCode, String userName, String profileImage) {
        this.id = id;
        this.userCode = userCode;
        this.userName = userName;
        this.profileImage = profileImage;
    }
}
