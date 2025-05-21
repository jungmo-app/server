package jungmo.server.domain.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jungmo.server.domain.dto.response.UserChattingRoomsResponse;
import jungmo.server.global.result.ResultDetailResponse;

@Tag(name = "ChattingRoom", description = "채팅방 관련 API")
public interface ChattingRoomSwaggerController {

    @Operation(summary = "참여중인 채팅방 목록 조회 API", description = "현재 회원이 참여중인 채팅방 목록을 조회합니다.")
    ResultDetailResponse<UserChattingRoomsResponse> getSubscribedChatRooms();
}
