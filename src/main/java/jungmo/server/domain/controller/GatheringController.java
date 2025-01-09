package jungmo.server.domain.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jungmo.server.domain.dto.request.GatheringRequest;
import jungmo.server.domain.dto.response.GatheringListResponse;
import jungmo.server.domain.dto.response.GatheringResponse;
import jungmo.server.domain.entity.Gathering;
import jungmo.server.domain.service.GatheringService;
import jungmo.server.global.result.ResultCode;
import jungmo.server.global.result.ResultDetailResponse;
import jungmo.server.global.result.ResultListResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/gatherings")
public class GatheringController implements GatheringSwaggerController {

    private final GatheringService gatheringService;

    @Override
    @PostMapping
    public ResultDetailResponse<String> saveGathering(@RequestBody @Valid GatheringRequest gatheringDto) {
        return new ResultDetailResponse<>(ResultCode.REGISTER_GATHERING, String.valueOf(gatheringService.saveGathering(gatheringDto)));
    }
    @Override
    @PutMapping("/{gatheringId}")
    public ResultDetailResponse<GatheringResponse> updateGathering(@PathVariable Long gatheringId,
                                                                   @RequestBody @Valid GatheringRequest gatheringDto) {
        gatheringService.updateGathering(gatheringId, gatheringDto);
        return new ResultDetailResponse<>(ResultCode.UPDATE_GATHERING,
                gatheringService.toDto(
                        gatheringService.findGathering(gatheringId)));
    }
    @Override
    @GetMapping("/{gatheringId}")
    public ResultDetailResponse<GatheringResponse> getGathering(@PathVariable Long gatheringId) {
        return new ResultDetailResponse<>(ResultCode.GET_GATHERING,
                gatheringService.toDto(
                        gatheringService.findGathering(gatheringId)));
    }
    @Override
    @GetMapping
    public ResultListResponse<GatheringListResponse> getMyGathering(){
        return new ResultListResponse<>(ResultCode.GET_MY_ALL_GATHERINGS, gatheringService.findMyGatherings());
    }
    @Override
    @DeleteMapping("/{gatheringId}")
    public ResultDetailResponse<Void> deleteGathering(@PathVariable Long gatheringId) {
        gatheringService.deleteGathering(gatheringId);
        return new ResultDetailResponse<>(ResultCode.DELETE_GATHERING, null);
    }

}
