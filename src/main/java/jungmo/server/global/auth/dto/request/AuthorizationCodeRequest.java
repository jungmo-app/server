package jungmo.server.global.auth.dto.request;

import lombok.Data;

@Data
public class AuthorizationCodeRequest {
    private String authorizationCode;
}