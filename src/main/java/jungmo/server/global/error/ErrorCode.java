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
    USER_NOT_EXISTS(400, "C009", "존재하지 않는 회원입니다."),
    IMAGE_UPLOAD_FAIL(400, "C010", "프로필 이미지를 수정하는데 실패했습니다"),
    USER_INVALID(400, "C011", "존재하지 않는 회원이 있습니다."),
    PASSWORD_NOT_MATCH(400, "C012", "현재 비밀번호가 일치하지 않습니다."),
    INVALID_PASSWORD(400, "C013", "새로운 비밀번호는 기존 비밀번호와 동일할 수 없습니다."),
    UNABLE_TO_UPDATE_PASSWORD(400, "C014", "소셜로그인 한 사용자입니다."),
    ALREADY_LOGOUT_TOKEN(400, "C015", "로그아웃상태인 토큰입니다."),
    SOCIAL_UNLINK_FAILED(400,"C016","카카오 연동 해제 실패."),
    USER_DELETED(400,"C017","이미 탈퇴한 유저입니다."),

    //Token
    TOKEN_EXPIRED(401, "T001", "Token has expired"),
    INVALID_TOKEN(401, "T002", "Invalid token"),
    MALFORMED_TOKEN(401, "T003", "Malformed token"),
    UNSUPPORTED_TOKEN(401, "T004", "Unsupported token"),

    //Gathering
    GATHERING_NOT_EXISTS(404,"G001","gathering does not exists"),
    NOT_HAVE_WRITE_AUTHORITY(404, "G002", "모임을 수정 할 수 있는 권한이 없습니다."),
    GATHERING_ALREADY_DELETED(404,"G003","이미 삭제된 모임입니다."),
    NO_AUTHORITY(404, "G004", "모임을 수정할 수 있는 권한이 없습니다."),
    ALREADY_CHOOSE(404, "G005", "이미 모임의 참석자이거나 거절한 모임입니다."),
    INVITATION_NOT_EXISTS(404, "G006", "수락 할 초대가 존재하지 않습니다."),


    //GatheringUser
    NOT_A_GATHERING_USER(404, "G007", "모임에 포함되지 않은 유저입니다."),
    GATHERING_USER_NOT_EXISTS(404, "G008", "해당 모임참석자가 존재하지 않습니다."),
    CANNOT_INVITE_SELF(404, "G009", "자기자신은 초대할 수 없습니다."),

    //GatheringLocation
    GATHERING_LOCATION_NOT_EXISTS(404,"GL001","해당하는 모임장소가 존재하지 않습니다"),
    INVALID_RELATION(404, "GL002", "해당 모임장소가 해당 모임에 포함되어 있지 않습니다."),

    //Location
    LOCATION_NOT_EXISTS(404,"L001","해당하는 장소가 존재하지 않습니다");
    private int status;
    private final String code;
    private final String message;
}
