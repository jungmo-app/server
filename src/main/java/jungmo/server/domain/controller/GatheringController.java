package jungmo.server.domain.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jungmo.server.domain.dto.request.GatheringDto;
import jungmo.server.domain.dto.request.GatheringUserDto;
import jungmo.server.domain.dto.response.GatheringListResponseDto;
import jungmo.server.domain.dto.response.GatheringResponseDto;
import jungmo.server.domain.entity.Authority;
import jungmo.server.domain.entity.Gathering;
import jungmo.server.domain.entity.GatheringStatus;
import jungmo.server.domain.service.GatheringService;
import jungmo.server.domain.service.GatheringUserService;
import jungmo.server.global.result.ResultCode;
import jungmo.server.global.result.ResultDetailResponse;
import jungmo.server.global.result.ResultListResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/gatherings")
@Tag(name = "Gathering", description = "모임 관련 API")
public class GatheringController {

    private final GatheringService gatheringService;

    @PostMapping
    @Operation(summary = "모임생성 API", description = "모임을 생성하는 API, 모임을 만든사람은 write 권한을 갖는다.")
    public ResultDetailResponse<String> saveGathering(@RequestBody @Valid GatheringDto gatheringDto) {
        Gathering gathering = gatheringService.saveGathering(gatheringDto);
        return new ResultDetailResponse<>(ResultCode.REGISTER_GATHERING, String.valueOf(gathering.getId()));
    }

    @PutMapping("/{gathering-id}")
    @Operation(summary = "모임수정 API", description = "모임 수정하는 API, write권한을 가진 사람만 수정이 가능하다.")
    public ResultDetailResponse<GatheringResponseDto> updateGathering(@PathVariable Long gathering_id,
                                                                      @RequestBody @Valid GatheringDto gatheringDto) {
        gatheringService.updateGathering(gathering_id, gatheringDto);
        Gathering gathering = gatheringService.findGathering(gathering_id);
        GatheringResponseDto dto = gatheringService.toDto(gathering);
        return new ResultDetailResponse<>(ResultCode.UPDATE_GATHERING, dto);
    }

    @GetMapping("/{gathering-id}")
    @Operation(summary = "모임 조회 API", description = "모임의 상세조회 API.")
    public ResultDetailResponse<GatheringResponseDto> getGathering(@PathVariable Long gathering_id) {
        Gathering gathering = gatheringService.findGathering(gathering_id);
        GatheringResponseDto dto = gatheringService.toDto(gathering);
        return new ResultDetailResponse<>(ResultCode.GET_GATHERING, dto);
    }

    @GetMapping
    @Operation(summary = "모임 목록 조회 API" , description = "로그인 된 사용자에게 예정된 모임들을 모두 조회한다.")
    public ResultListResponse<GatheringListResponseDto> getMyGathering(){
        List<GatheringListResponseDto> myGatherings = gatheringService.findMyGatherings();
        return new ResultListResponse<>(ResultCode.GET_MY_ALL_GATHERINGS, myGatherings);
    }

    @DeleteMapping("/{gathering-id}")
    @Operation(summary = "모임 삭제 API", description = "모임을 삭제하는 API, write권한을 가진 사람만 삭제가 가능하다.")
    public ResultDetailResponse<Void> deleteGathering(@PathVariable Long gathering_id) {
        gatheringService.deleteGathering(gathering_id);
        return new ResultDetailResponse<>(ResultCode.DELETE_GATHERING, null);
    }

}
