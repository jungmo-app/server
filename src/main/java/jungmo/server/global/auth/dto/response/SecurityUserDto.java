package jungmo.server.global.auth.dto.response;

import jungmo.server.global.auth.service.PrincipalDetails;
import lombok.*;

@NoArgsConstructor
@Getter
@ToString
@AllArgsConstructor
@Builder
public class SecurityUserDto {
    private String email;
    private String nickname;
    private Long userId;

    public static SecurityUserDto from(PrincipalDetails principal) {
        return SecurityUserDto.builder()
                .email(principal.getUsername())
                .nickname(principal.getUser().getUserName()) // User 객체에서 닉네임 가져오기
                .userId(principal.getUser().getId())        // User 객체에서 ID 가져오기
                .build();
    }

}
