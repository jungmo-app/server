package jungmo.server.global.error;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    // Common
    INTERNAL_SERVER_ERROR(500, "C001", "internal server error"),
    INVALID_INPUT_VALUE(400, "C002", "invalid input type"),
    METHOD_NOT_ALLOWED(405, "C003", "method not allowed"),
    INVALID_TYPE_VALUE(400, "C004", "invalid type value"),
    BAD_CREDENTIALS(400, "C005", "bad credentials"),
    AUTHENTICATION_FAILED(400, "C006", "인증에 실패하였습니다"),

    //Token
    TOKEN_EXPIRED(401, "T001", "Token has expired"),
    INVALID_TOKEN(401, "T002", "Invalid token"),
    MALFORMED_TOKEN(401, "T003", "Malformed token"),
    UNSUPPORTED_TOKEN(401, "T004", "Unsupported token");


    private int status;
    private final String code;
    private final String message;
}
