package jungmo.server.global.auth.service;

import jungmo.server.global.auth.dto.response.KakaoUserResponse;
import jungmo.server.global.error.ErrorCode;
import jungmo.server.global.error.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Map;


@Slf4j
@Service
@RequiredArgsConstructor
public class KakaoService {

    @Value("${kakao.admin-key}")
    private String adminKey;

    @Value("${spring.security.oauth2.client.registration.kakao.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.kakao.client-secret}")
    private String clientSecret;

    @Value("${kakao.redirect-uri}")
    private String redirectUri;

    private final RestTemplate restTemplate;

    public void unlinkKakaoAccount(String kakaoId) {
        String url = "https://kapi.kakao.com/v1/user/unlink";

        // HTTP 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(adminKey); // 관리자 키 설정
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        // 요청 바디 설정
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("target_id", kakaoId);
        body.add("target_id_type", "user_id");

        // 요청 생성
        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(body, headers);

        // 요청 전송
        ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);

        // 응답 확인
        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new BusinessException(ErrorCode.SOCIAL_UNLINK_FAILED);
        }
    }

    public KakaoUserResponse getUserEmail(String accessToken) {
        log.info("accessToken : {}", accessToken);

        // 사용자 정보 요청
        HttpHeaders userHeaders = new HttpHeaders();
        userHeaders.setBearerAuth(accessToken);
        HttpEntity<Void> userRequest = new HttpEntity<>(userHeaders);

        ResponseEntity<Map> userResponse = restTemplate.exchange(
                "https://kapi.kakao.com/v2/user/me",
                HttpMethod.GET,
                userRequest,
                Map.class
        );

        Map userMap = userResponse.getBody();
        log.info("userMap : {}", userMap);

        Long kakaoId = ((Number) userMap.get("id")).longValue();

        Map account = (Map) userMap.get("kakao_account");
        String email = (String) account.get("email");
        Map profile = (Map) account.get("profile");
        String nickname = (String) profile.get("nickname");

        return new KakaoUserResponse(email, kakaoId, nickname);
    }
}
