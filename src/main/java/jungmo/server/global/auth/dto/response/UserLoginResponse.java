package jungmo.server.global.auth.dto.response;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserLoginResponse {

    private Long userId;
    private String userCode;
    private String userName;
    private String profileImage;
    private String provider;
    private String accessToken;

    public UserLoginResponse(Long userId, String userCode, String userName, String profileImage, String provider, String accessToken) {
        this.userId = userId;
        this.userCode = userCode;
        this.userName = userName;
        this.profileImage = profileImage;
        this.provider = provider;
        this.accessToken = accessToken;
    }
}
