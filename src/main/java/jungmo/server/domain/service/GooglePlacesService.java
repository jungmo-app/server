package jungmo.server.domain.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jungmo.server.domain.dto.request.PositionRequest;
import jungmo.server.domain.dto.response.PlaceAutoCompleteDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class GooglePlacesService {

    private final RestTemplate restTemplate;

    @Value("${google.api.key}") // 환경 변수에서 API 키 가져오기
    private String googleApiKey;

    public List<PlaceAutoCompleteDto> getAutocompleteResults(String input, String language) {
        String lang = (language != null) ? language : "ko";
        String url = "https://maps.googleapis.com/maps/api/place/autocomplete/json" +
                "?input=" + input +
                "&key=" + googleApiKey +
                "&language=" + lang +
                "&components=country:KR";

        try {
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

            if (response.getBody() == null) {
                log.error("❌ Google Places API 응답이 비어 있음.");
                return Collections.emptyList();
            }

            // JSON 파싱을 Jackson으로 변경
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode root = objectMapper.readTree(response.getBody());
            JsonNode predictions = root.path("predictions");
            log.info("predictions = {}",predictions);

            List<PlaceAutoCompleteDto> results = new ArrayList<>();
            for (JsonNode prediction : predictions) {
                PlaceAutoCompleteDto place = new PlaceAutoCompleteDto(
                        prediction.path("structured_formatting").path("main_text").asText(),
                        prediction.path("place_id").asText());
                results.add(place);
            }
            return results;

        } catch (Exception e) {
            log.error("❌ Google Places API 요청 실패: {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    public List<PlaceAutoCompleteDto> getAutocompleteResultsWithPosition(String input, String language, PositionRequest positionRequest) {
        String lang = (language != null) ? language : "ko";
        String url = "https://maps.googleapis.com/maps/api/place/autocomplete/json" +
                "?input=" + input +
                "&key=" + googleApiKey +
                "&language=" + lang +
                "&components=country:KR" +
                "&location=" + positionRequest.getLatitude() + "," + positionRequest.getLongitude() + "&radius=5000";

        try {
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

            if (response.getBody() == null) {
                log.error("❌ Google Places API 응답이 비어 있음.");
                return Collections.emptyList();
            }

            // JSON 파싱을 Jackson으로 변경
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode root = objectMapper.readTree(response.getBody());
            JsonNode predictions = root.path("predictions");
            log.info("predictions = {}",predictions);

            List<PlaceAutoCompleteDto> results = new ArrayList<>();
            for (JsonNode prediction : predictions) {
                PlaceAutoCompleteDto place = new PlaceAutoCompleteDto(
                        prediction.path("structured_formatting").path("main_text").asText(),
                        prediction.path("place_id").asText());
                results.add(place);
            }
            return results;

        } catch (Exception e) {
            log.error("❌ Google Places API 요청 실패: {}", e.getMessage());
            return Collections.emptyList();
        }
    }
}
