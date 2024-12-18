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
    BAD_CREDENTIALS(400, "C005", "invalid email or password"),
    INVALID_REFRESH_TOKEN(400, "C006", "Invalid or expired refresh token"),
    AUTHENTICATION_FAILED(400, "C007", "인증에 실패하였습니다"),
    EMAIL_ALREADY_EXISTS(400, "C008", "이미 존재하는 이메일 입니다"),

    //Token
    TOKEN_EXPIRED(401, "T001", "Token has expired"),
    INVALID_TOKEN(401, "T002", "Invalid token"),
    MALFORMED_TOKEN(401, "T003", "Malformed token"),
    UNSUPPORTED_TOKEN(401, "T004", "Unsupported token"),

    //Gathering
    GATHERING_NOT_EXISTS(404,"G001","gathering does not exists"),
    NOT_HAVE_WRITE_AUTHORITY(404, "G002", "모임을 수정 할 수 있는 권한이 없습니다."),
    GATHERING_ALREADY_DELETED(404,"G003","이미 삭제된 모임입니다.");


    private int status;
    private final String code;
    private final String message;
}
