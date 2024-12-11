package jungmo.server.global.auth.dto.response;


import lombok.Data;

@Data
public class KakaoTokenResponse {
    private String accessToken;
    private String refreshToken;
}