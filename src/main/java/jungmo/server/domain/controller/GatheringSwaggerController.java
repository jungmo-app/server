package jungmo.server.domain.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jungmo.server.domain.dto.request.GatheringRequest;
import jungmo.server.domain.dto.response.GatheringListResponse;
import jungmo.server.domain.dto.response.GatheringResponse;
import jungmo.server.global.result.ResultDetailResponse;
import jungmo.server.global.result.ResultListResponse;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "Gathering", description = "모임 관련 API")
public interface GatheringSwaggerController {

    @Operation(summary = "모임생성 API", description = "모임을 생성하는 API, 모임을 만든사람은 write 권한을 갖는다.")
    public ResultDetailResponse<String> saveGathering(@RequestBody @Valid GatheringRequest gatheringDto);

    @Operation(summary = "모임수정 API", description = "모임 수정하는 API, write권한을 가진 사람만 수정이 가능하다.")
    public ResultDetailResponse<GatheringResponse> updateGathering(@PathVariable Long gatheringId,
                                                                   @RequestBody @Valid GatheringRequest gatheringDto);

    @Operation(summary = "모임 조회 API", description = "모임의 상세조회 API.")
    public ResultDetailResponse<GatheringResponse> getGathering(@PathVariable Long gatheringId);

    @Operation(summary = "모임 목록 조회 API" , description = "로그인 된 사용자에게 예정된 모임들을 모두 조회한다.")
    public ResultListResponse<GatheringListResponse> getMyGathering();

    @Operation(summary = "모임 삭제 API", description = "모임을 삭제하는 API, write권한을 가진 사람만 삭제가 가능하다.")
    public ResultDetailResponse<Void> deleteGathering(@PathVariable Long gatheringId);
}
