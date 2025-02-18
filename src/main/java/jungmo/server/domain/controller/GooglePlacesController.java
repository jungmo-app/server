package jungmo.server.domain.controller;

import jungmo.server.domain.dto.response.PlaceAutoCompleteDto;
import jungmo.server.domain.service.GooglePlacesService;
import jungmo.server.global.result.ResultCode;
import jungmo.server.global.result.ResultListResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@Slf4j
@RestController
@RequestMapping("/maps")
@RequiredArgsConstructor
public class GooglePlacesController implements GooglePlacesSwaggerController{

    private final GooglePlacesService googlePlacesService;

    @Override
    @GetMapping("/autocomplete")
    public ResultListResponse<PlaceAutoCompleteDto> getAutocomplete(
            @RequestParam String input,
            @RequestParam(required = false) String language) {

        log.info("🔍 Received input: '{}'", input);  // input 값 로그 확인

        return new ResultListResponse<>(ResultCode.GET_AUTO_COMPLETE, googlePlacesService.getAutocompleteResults(input, language));
    }
}
