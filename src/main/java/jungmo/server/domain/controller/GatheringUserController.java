package jungmo.server.domain.controller;

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
    public ResultDetailResponse<Void> inviteUser(@PathVariable Long gathering_id,
                                                 @PathVariable Long user_id) {
        gatheringUserService.inviteUser(gathering_id,user_id);
        return new ResultDetailResponse<>(ResultCode.INVITE_USER_SUCCESS, null);
    }


}
