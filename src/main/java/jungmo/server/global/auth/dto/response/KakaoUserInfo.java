package jungmo.server.global.auth.dto.response;

import lombok.Data;

@Data
public class KakaoUserInfo {
    private String id;
    private String email;
    private String nickname;
}
