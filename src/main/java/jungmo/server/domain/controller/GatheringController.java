package jungmo.server.domain.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jungmo.server.domain.dto.request.GatheringDto;
import jungmo.server.domain.dto.response.GatheringResponseDto;
import jungmo.server.domain.entity.Gathering;
import jungmo.server.domain.service.GatheringService;
import jungmo.server.global.result.ResultCode;
import jungmo.server.global.result.ResultDetailResponse;
import jungmo.server.global.result.ResultListResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/gathering")
@Tag(name = "Gathering", description = "모임 관련 API")
public class GatheringController {

    private final GatheringService gatheringService;

    @PostMapping("/save")
    @Operation(summary = "모임생성 API", description = "모임을 생성하는 API, 모임을 만든사람은 write 권한을 갖는다.")
    public ResultDetailResponse<String> saveGathering(@RequestBody @Valid GatheringDto gatheringDto) {
        Long savedId = gatheringService.saveGathering(gatheringDto);
        return new ResultDetailResponse<>(ResultCode.REGISTER_GATHERING, String.valueOf(savedId));
    }

    @PostMapping("/{gathering_id}/update")
    @Operation(summary = "모임수정 API", description = "모임 수정하는 API, write권한을 가진 사람만 수정이 가능하다.")
    public ResultDetailResponse<GatheringResponseDto> updateGathering(@PathVariable Long gathering_id,
                                                                      @RequestBody @Valid GatheringDto gatheringDto) {
        gatheringService.updateGathering(gathering_id, gatheringDto);
        Gathering gathering = gatheringService.findGathering(gathering_id);
        GatheringResponseDto dto = gathering.toDto();
        return new ResultDetailResponse<>(ResultCode.UPDATE_GATHERING, dto);
    }




}
