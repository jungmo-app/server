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
public class GatheringLocationController implements GatheringLocationSwaggerController{

    private final GatheringLocationService gatheringLocationService;

    @Override
    @PostMapping("/{gatheringId}/locations")
    public ResultDetailResponse<Long> saveLocation(@PathVariable Long gatheringId,
                                                   @RequestBody @Valid LocationRequest locationRequestDto) {
        return new ResultDetailResponse<>(ResultCode.REGISTER_GATHERING_LOCATION, gatheringLocationService.saveGatheringLocation(gatheringId, locationRequestDto,false).getId());
    }

    @Override
    @DeleteMapping("/{gatheringId}/locations/{locationId}")
    public ResultDetailResponse<Void> deleteLocation(@PathVariable Long gatheringId, @PathVariable Long locationId) {
        gatheringLocationService.deleteGatheringLocation(gatheringId, locationId);
        return new ResultDetailResponse<>(ResultCode.DELETE_GATHERING_LOCATION, null);
    }

}
