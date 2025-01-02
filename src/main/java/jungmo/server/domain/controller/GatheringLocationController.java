package jungmo.server.domain.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jungmo.server.domain.dto.request.LocationRequestDto;
import jungmo.server.domain.service.GatheringLocationService;
import jungmo.server.global.result.ResultCode;
import jungmo.server.global.result.ResultDetailResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/gatherings")
@Tag(name = "GatheringLocation", description = "모임 장소 관련 API")
public class GatheringLocationController {

    private final GatheringLocationService gatheringLocationService;

    @PostMapping("/{gathering_id}/locations")
    @Operation(summary = "모임장소 저장 API", description = "모임장소를 저장하는 api")
    public ResultDetailResponse<Void> saveLocation(@PathVariable Long gathering_id,
                                                   @RequestBody @Valid LocationRequestDto locationRequestDto) {
        gatheringLocationService.saveGatheringLocation(gathering_id, locationRequestDto,false);
        return new ResultDetailResponse<>(ResultCode.REGISTER_GATHERING_LOCATION, null);
    }

    @DeleteMapping("/{gathering_id}/locations/{location_id}")
    @Operation(summary = "모임 장소 삭제 API", description = "모임 장소를 삭제하는 api")
    public ResultDetailResponse<Void> deleteLocation(@PathVariable Long gathering_id, @PathVariable Long location_id) {
        gatheringLocationService.deleteGatheringLocation(gathering_id, location_id);
        return new ResultDetailResponse<>(ResultCode.DELETE_GATHERING_LOCATION, null);
    }

}
