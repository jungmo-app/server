package jungmo.server.global.auth.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class KakaoUserResponse {
    private String email;
    private Long kakaoId;
    private String nickName;
}
