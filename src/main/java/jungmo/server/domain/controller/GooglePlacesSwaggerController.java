package jungmo.server.domain.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jungmo.server.domain.dto.request.PositionRequest;
import jungmo.server.domain.dto.response.PlaceAutoCompleteDto;
import jungmo.server.global.result.ResultListResponse;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;


@Tag(name = "구글맵 관련", description = "구글 맵 요청관련 API")
public interface GooglePlacesSwaggerController {

    @Operation(summary = "연관검색어 반환 API", description = "input에 맞는 연관검색어를 반환해주는 api")
    public ResultListResponse<PlaceAutoCompleteDto> getAutocomplete(
            @RequestParam String input,
            @RequestParam(required = false) String language);

    @Operation(summary = "사용자 위치 기반 연관검색어 반환 API", description = "사용자의 위치를 기반으로 input에 맞는 연관검색어를 반환해주는 api")
    public ResultListResponse<PlaceAutoCompleteDto> getAutocompleteWithPosition(
            @RequestParam String input,
            @RequestParam(required = false) String language,
            @RequestBody PositionRequest positionRequest);
}
