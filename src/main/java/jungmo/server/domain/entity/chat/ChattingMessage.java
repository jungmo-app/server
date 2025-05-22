package jungmo.server.domain.entity.chat;

import jakarta.persistence.Id;
import java.time.LocalDateTime;
import java.util.List;
import jungmo.server.domain.dto.response.chat.ChatMessageResponse;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "chatting_message")
@Data
public class ChattingMessage {

    @Id
    private String id;

    private MessageType type;

    private Long roomId;

    private Long senderId;
    private String senderName;

    private String content;
    private List<String> imageUrls;

    private LocalDateTime sendTime;

    private ChattingMessage(MessageType type, Long roomId, Long senderId, String senderName, String content, List<String> imageUrls, LocalDateTime sendTime) {
        this.type = type;
        this.roomId = roomId;
        this.senderId = senderId;
        this.senderName = senderName;
        this.content = content;
        this.imageUrls = imageUrls;
        this.sendTime = sendTime;
    }

    public static ChattingMessage create(MessageType type, Long roomId, Long senderId, String senderName,  String message, List<String> imageUrls, LocalDateTime sendTime) {
        return new ChattingMessage(type, roomId, senderId, senderName, message, imageUrls, sendTime);
    }

}
