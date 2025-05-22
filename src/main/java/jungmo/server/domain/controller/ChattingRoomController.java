package jungmo.server.domain.controller;

import jungmo.server.domain.dto.response.chat.UserChattingRoomsResponse;
import jungmo.server.domain.service.ChattingRoomService;
import jungmo.server.global.result.ResultCode;
import jungmo.server.global.result.ResultDetailResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/gatherings/chatting-rooms")
public class ChattingRoomController implements ChattingRoomSwaggerController {

    private final ChattingRoomService chattingRoomService;

    @Override
    @GetMapping
    public ResultDetailResponse<UserChattingRoomsResponse> getSubscribedChatRooms() {
        return new ResultDetailResponse<>(ResultCode.GET_CHATTING_ROOM,
            chattingRoomService.getChattingRoomIdsByUserId());
    }

}
