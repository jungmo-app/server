package jungmo.server.global.auth.service;

import jungmo.server.global.error.ErrorCode;
import jungmo.server.global.error.exception.BusinessException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;


@Service
public class KakaoService {

    @Value("${kakao.admin-key}")
    private String adminKey;

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
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);

        // 응답 확인
        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new BusinessException(ErrorCode.SOCIAL_UNLINK_FAILED);
        }
    }
}
