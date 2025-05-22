package jungmo.server.domain.dto.request.chat;

import java.time.LocalDateTime;
import java.util.List;
import jungmo.server.domain.entity.chat.MessageType;
import lombok.Data;

@Data
public class ChattingMessageRequest {

    private Long roomId;
    private MessageType messageType;
    private String message;
    private LocalDateTime sendTime;
    private List<String> imageUrls;

}
