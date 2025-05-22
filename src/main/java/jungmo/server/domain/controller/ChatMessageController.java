package jungmo.server.domain.controller;

import java.io.IOException;
import java.util.List;
import jungmo.server.domain.dto.PageResponse;
import jungmo.server.domain.dto.request.chat.ChattingMessageRequest;
import jungmo.server.domain.dto.response.chat.ChatMessageResponse;
import jungmo.server.domain.dto.response.chat.MessageImagesUrlResponse;
import jungmo.server.domain.service.chat.ChatMessageService;
import jungmo.server.global.auth.service.S3Service;
import jungmo.server.global.result.ResultCode;
import jungmo.server.global.result.ResultDetailResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
public class ChatMessageController {

    private final SimpMessageSendingOperations sendingOperations;
    private final ChatMessageService chatMessageService;
    private final S3Service s3Service;

    @MessageMapping("/room/{roomId}")
    public void test(@DestinationVariable("roomId") Long roomId,
        ChattingMessageRequest chattingMessageRequest) {
//        @AuthenticationPrincipal PrincipalDetails principal) {

        chatMessageService.processChatMessages(chattingMessageRequest, roomId);
    }

    @PostMapping(value = "/chat/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResultDetailResponse<MessageImagesUrlResponse> uploadChatImages(
        @RequestPart("images") List<MultipartFile> files) throws IOException {

        return new ResultDetailResponse<>(ResultCode.REGISTER_CHAT_IMAGES,
            new MessageImagesUrlResponse(s3Service.uploadImages(files)));
    }


    @GetMapping("/gathering/chatting-rooms/{roomId}")
    public ResultDetailResponse<PageResponse<ChatMessageResponse>> getChatMessages(
        @PathVariable("roomId") Long roomId, Pageable pageable) {

        return new ResultDetailResponse<>(ResultCode.GET_CHATTING_MESSAGES,
            new PageResponse<>(chatMessageService.getChatMessagesByRoomId(roomId, pageable)));
    }
}
