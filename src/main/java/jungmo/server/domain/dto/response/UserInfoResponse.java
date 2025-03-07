package jungmo.server.domain.dto.response;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserInfoResponse {

    private Long userId;
    private String userCode;
    private String userName;
    private String profileImage;
    private String provider;

    public UserInfoResponse(Long id, String userCode, String userName, String profileImage, String provider) {
        this.userId = id;
        this.userCode = userCode;
        this.userName = userName;
        this.profileImage = profileImage;
        this.provider = provider;
    }
}
