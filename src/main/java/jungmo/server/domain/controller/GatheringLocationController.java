package jungmo.server.domain.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jungmo.server.domain.dto.request.LocationRequest;
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

    @PostMapping("/{gatheringId}/locations")
    @Operation(summary = "모임장소 저장 API", description = "모임장소를 저장하는 api")
    public ResultDetailResponse<Long> saveLocation(@PathVariable Long gatheringId,
                                                   @RequestBody @Valid LocationRequest locationRequestDto) {
        return new ResultDetailResponse<>(ResultCode.REGISTER_GATHERING_LOCATION, gatheringLocationService.saveGatheringLocation(gatheringId, locationRequestDto,false).getId());
    }

    @DeleteMapping("/{gatheringId}/locations/{locationId}")
    @Operation(summary = "모임 장소 삭제 API", description = "모임 장소를 삭제하는 api")
    public ResultDetailResponse<Void> deleteLocation(@PathVariable Long gatheringId, @PathVariable Long locationId) {
        gatheringLocationService.deleteGatheringLocation(gatheringId, locationId);
        return new ResultDetailResponse<>(ResultCode.DELETE_GATHERING_LOCATION, null);
    }

}
