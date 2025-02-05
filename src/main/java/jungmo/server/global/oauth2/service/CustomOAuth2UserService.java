package jungmo.server.global.oauth2.service;

import jungmo.server.domain.entity.User;
import jungmo.server.domain.repository.UserRepository;
import jungmo.server.domain.service.UserService;
import jungmo.server.global.error.ErrorCode;
import jungmo.server.global.error.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final UserRepository userRepository;
    private final UserService userService;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) {
        // 1. OAuth2User 기본 정보 로드
        OAuth2User oAuth2User = new DefaultOAuth2UserService().loadUser(userRequest);

        // 2. 제공자 정보 및 사용자 정보 추출
        String provider = userRequest.getClientRegistration().getRegistrationId(); // 예: "kakao"
        Long kakaoId = oAuth2User.getAttribute("id"); // 카카오 고유 사용자 ID
        Map<String, Object> kakaoAccount = oAuth2User.getAttribute("kakao_account");
        Map<String, Object> properties = oAuth2User.getAttribute("properties");

        if (kakaoId == null || kakaoAccount == null || properties == null) {
            throw new IllegalArgumentException("OAuth2 인증에서 사용자 정보를 가져올 수 없습니다.");
        }

        // 이메일과 닉네임 추출
        String email = (String) kakaoAccount.get("email");
        String nickname = (String) properties.get("nickname");

        if (email == null) {
            throw new IllegalArgumentException("OAuth2 인증에서 이메일을 가져올 수 없습니다.");
        }

        // 사용자 속성에 email과 kakaoId 추가
        Map<String, Object> attributes = new HashMap<>(oAuth2User.getAttributes());
        attributes.put("email", email);
        attributes.put("kakaoId", kakaoId);

        // 3. 사용자 정보 DB 조회
        User user = userRepository.findByEmail(email).orElse(null);

        if (user != null) {
            // 4. 소프트 딜리트 상태 확인
            if (user.getIsDeleted()) {
                // 탈퇴된 사용자 복구
                user.reactivate(kakaoId.toString(),provider); // 상태를 ACTIVE로 변경
                userRepository.save(user);
            }
        } else {
            // 고유 코드 생성
            String uniqueCode = userService.generateUniqueUserCode();
            // 5. 신규 사용자 저장
            user = userRepository.save(User.builder()
                    .email(email)
                    .userCode(uniqueCode)
                    .oauthId(kakaoId.toString())
                    .userName(nickname)
                    .provider(provider)
                    .role("ROLE_USER")
                    .build());

        }
        // 4. DefaultOAuth2User 객체 반환
        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority(user.getRole())), // 권한 설정
                attributes, // 사용자 속성
                "email" // 이메일을 기본 식별자로 설정
        );
    }
}
