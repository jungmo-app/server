package jungmo.server.domain.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jungmo.server.domain.dto.response.PlaceAutoCompleteDto;
import jungmo.server.global.result.ResultListResponse;
import org.springframework.web.bind.annotation.RequestParam;


@Tag(name = "구글맵 관련", description = "구글 맵 요청관련 API")
public interface GooglePlacesSwaggerController {

    @Operation(summary = "연관검색어 반환 API", description = "input에 맞는 연관검색어를 반환해주는 api")
    public ResultListResponse<PlaceAutoCompleteDto> getAutocomplete(
            @RequestParam String input,
            @RequestParam(required = false) String language);
}
