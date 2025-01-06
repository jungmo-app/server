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
    GET_MY_INFO_SUCCESS(200, "U005", "로그인된 사용자 정보 조회 완료"),
    UPDATE_USER_INFO(200, "U006", "사용자 정보 수정 완료"),
    UPDATE_USER_PASSWORD(200, "U007", "사용자 비밀번호 수정 완료"),

    //Gathering
    REGISTER_GATHERING(200,"G001","모임 생성 완료"),
    UPDATE_GATHERING(200,"G002","모임 수정 완료"),
    GET_GATHERING(200, "G003", "모임 조회 완료"),
    GET_MY_ALL_GATHERINGS(200, "G004", "나의 모든 모임 조회 완료"),
    DELETE_GATHERING(200, "G005", "모임 삭제 완료"),
    INVITE_USER_SUCCESS(200, "G006", "모임 초대 완료"),
    ACCEPT_INVITATION(200, "G007", "모임의 초대 수락 완료"),
    REJECT_INVITATION(200, "G008", "모임의 초대 거절 완료"),
    GET_ALL_GATHERING_USERS(200, "G009", "모임의 모든 참석자 조회 완료"),
    EXPORT_SUCCESS(200, "G010", "모임의 참석자 내보내기 완료"),

    //GatheringLocation
    REGISTER_GATHERING_LOCATION(200,"GL001","모임장소 저장 완료"),
    DELETE_GATHERING_LOCATION(200,"GL002","모임장소 삭제 완료");

    private int status;
    private final String code;
    private final String message;

}
