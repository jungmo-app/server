package jungmo.server.domain.dto.request.chat;

import java.time.LocalDateTime;
import jungmo.server.domain.entity.MessageType;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class ChattingMessageRequest {

    private Long roomId;
    private MessageType messageType;
    private Long senderId;
    private String message;
    private LocalDateTime sendTime;
    private MultipartFile images;

}
