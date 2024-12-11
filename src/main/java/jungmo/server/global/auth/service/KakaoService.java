package jungmo.server.global.auth.service;

import jungmo.server.global.auth.dto.response.KakaoTokenResponse;
import jungmo.server.global.auth.dto.response.KakaoUserInfo;
import jungmo.server.global.util.KakaoProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;


@Service
@RequiredArgsConstructor
public class KakaoService {

    private final RestTemplate restTemplate;
    private final KakaoProperties kakaoProperties;

    public KakaoUserInfo getUserInfo(String authorizationCode) {
        // Access Token 요청
        KakaoTokenResponse tokenResponse = getAccessToken(authorizationCode);

        // 사용자 정보 요청
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(tokenResponse.getAccessToken());
        HttpEntity<Void> request = new HttpEntity<>(headers);

        ResponseEntity<KakaoUserInfo> response = restTemplate.exchange(
                kakaoProperties.getUserInfoUrl(),
                HttpMethod.GET,
                request,
                KakaoUserInfo.class
        );

        return response.getBody();
    }

    private KakaoTokenResponse getAccessToken(String authorizationCode) {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", kakaoProperties.getClientId());
        params.add("client_secret", kakaoProperties.getClientSecret());
        params.add("redirect_uri", kakaoProperties.getRedirectUri());
        params.add("code", authorizationCode);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        ResponseEntity<KakaoTokenResponse> response = restTemplate.postForEntity(kakaoProperties.getAuthUrl(), request, KakaoTokenResponse.class);
        return response.getBody();
    }
}
