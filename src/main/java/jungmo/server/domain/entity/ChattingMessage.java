package jungmo.server.domain.entity;

import java.time.LocalDateTime;
import java.util.List;

public class ChattingMessage {

    private MessageType type;

    private String roomId;

    private String senderId;
    private String senderNickname;
    private String senderProfileUrl;

    private String content;
    private List<String> imageUrls;

    private LocalDateTime sendTime;
}
