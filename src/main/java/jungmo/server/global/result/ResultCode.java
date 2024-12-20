package jungmo.server.global.result;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ResultCode {
    // User
    REGISTER_SUCCESS(200,"U001","회원가입 완료"),
    LOGIN_SUCCESS(200, "U002", "로그인 완료"),
    REFRESH_SUCCESS(200, "U003", "토큰 재발급 완료"),
    GET_USER_SUCCESS(200, "U004", "유저 조회 완료"),

    //Gathering
    REGISTER_GATHERING(200,"G001","모임 생성 완료"),
    UPDATE_GATHERING(200,"G002","모임 수정 완료"),
    GET_GATHERING(200, "G003", "모임 조회 완료"),
    GET_MY_ALL_GATHERINGS(200, "G004", "나의 모든 모임 조회 완료"),
    DELETE_GATHERING(200, "G005", "모임 삭제 완료"),
    INVITE_USER_SUCCESS(200, "G006", "모임 초대 완료");

    private int status;
    private final String code;
    private final String message;

}
