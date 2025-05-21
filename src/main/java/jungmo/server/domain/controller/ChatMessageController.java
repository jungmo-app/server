package jungmo.server.domain.controller;

import jungmo.server.domain.dto.request.chat.ChattingMessageRequest;
import jungmo.server.domain.entity.MessageType;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ChatMessageController {

    private final SimpMessageSendingOperations sendingOperations;

    @MessageMapping("/room/{roomId}")
    public void test(@DestinationVariable("roomId") Long roomId, ChattingMessageRequest chattingMessageRequest) {
        if (MessageType.ENTER.equals(chattingMessageRequest.getMessageType())) {
            System.out.println("Room Id: " + roomId);
            System.out.println(chattingMessageRequest.getSenderId() + "이가 채팅방에 접속했습니다.");
        }
    }
}
