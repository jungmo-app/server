package jungmo.server.domain.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jungmo.server.domain.dto.request.LocationRequest;
import jungmo.server.global.result.ResultDetailResponse;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "GatheringLocation", description = "모임 장소 관련 API")
public interface GatheringLocationSwaggerController {

    @Operation(summary = "모임장소 저장 API", description = "모임장소를 저장하는 api")
    public ResultDetailResponse<Long> saveLocation(@PathVariable Long gatheringId,
                                                   @RequestBody @Valid LocationRequest locationRequestDto);

    @Operation(summary = "모임 장소 삭제 API", description = "모임 장소를 삭제하는 api")
    public ResultDetailResponse<Void> deleteLocation(@PathVariable Long gatheringId, @PathVariable Long locationId);
}
