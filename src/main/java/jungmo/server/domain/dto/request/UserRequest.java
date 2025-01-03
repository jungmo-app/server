package jungmo.server.domain.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class UserRequest {

    @NotBlank(message = "닉네임은 비어있을 수 없습니다.")
    private String userName;
    private MultipartFile profileImage;
}
