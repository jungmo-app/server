package jungmo.server.domain.dto.response.chat;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import jungmo.server.domain.entity.chat.MessageType;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ChatMessageResponse {

    Long roomId;
    MessageType messageType;
    Long senderId;
    String senderName;
    String content;
    List<String> imageUrls;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    LocalDate sendDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
    LocalTime sendTime;
}
