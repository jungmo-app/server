package jungmo.server.domain.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jungmo.server.domain.service.GatheringUserService;
import jungmo.server.global.result.ResultCode;
import jungmo.server.global.result.ResultDetailResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/gathering")
@Tag(name = "GatheringUser", description = "모임참석자 관련 API")
public class GatheringUserController {

    private final GatheringUserService gatheringUserService;

    @PostMapping("{gathering_id}/{user_id}/invite")
    @Operation(summary = "모임초대 API", description = "write권한을 가진 사용자가 유저를 초대 할 수 있다.")
    public ResultDetailResponse<Void> inviteUser(@PathVariable Long gathering_id,
                                                 @PathVariable Long user_id) {
        gatheringUserService.inviteUser(gathering_id,user_id);
        return new ResultDetailResponse<>(ResultCode.INVITE_USER_SUCCESS, null);
    }

    @PostMapping("/{gathering_id}/accept")
    @Operation(summary = "모임초대 수락 API", description = "모임초대 받은 유저가 수락하는 API, 모임ID String타입으로 반환할게요")
    public ResultDetailResponse<String> acceptInvitation(@PathVariable Long gathering_id) {
        gatheringUserService.acceptInvitation(gathering_id);
        return new ResultDetailResponse<>(ResultCode.ACCEPT_INVITATION, String.valueOf(gathering_id));
    }

    @PostMapping("/{gathering_id}/reject")
    @Operation(summary = "모임초대 거절 API", description = "모임초대 받은 유저가 거절하는 API")
    public ResultDetailResponse<Void> rejectInvitation(@PathVariable Long gathering_id) {
        gatheringUserService.rejectInvitation(gathering_id);
        return new ResultDetailResponse<>(ResultCode.REJECT_INVITATION, null);
    }


}