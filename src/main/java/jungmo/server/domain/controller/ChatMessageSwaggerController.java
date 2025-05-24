package jungmo.server.domain.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.io.IOException;
import java.security.Principal;
import java.util.List;
import jungmo.server.domain.dto.PageResponse;
import jungmo.server.domain.dto.request.chat.ChattingMessageRequest;
import jungmo.server.domain.dto.response.chat.ChatMessageResponse;
import jungmo.server.domain.dto.response.chat.MessageImagesUrlResponse;
import jungmo.server.global.result.ResultDetailResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "ChatMessage", description = "채팅 메시지 API")
public interface ChatMessageSwaggerController {

    @Operation(summary = "채팅 메시지 전송", description = "채팅방에 메시지를 전송합니다.")
    @MessageMapping("/room/{roomId}")
    void sendChatMessage(
        @Parameter(description = "채팅방 ID", example = "1") Long roomId,
        @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "채팅 메시지 요청 Body")
        ChattingMessageRequest request, Principal principal
    );


    @Operation(summary = "채팅 이미지 업로드", description = "채팅에서 사용할 이미지를 업로드합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "업로드 성공",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = MessageImagesUrlResponse.class)))
    })
    ResultDetailResponse<MessageImagesUrlResponse> uploadChatImages(
        @Parameter(description = "이미지 파일 목록") @RequestPart("images") List<MultipartFile> files
    ) throws IOException;



    @Operation(summary = "채팅 내역 조회", description = "채팅방의 채팅 내역을 조회합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회 성공",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ChatMessageResponse.class)))
    })
    ResultDetailResponse<PageResponse<ChatMessageResponse>> getChatMessages(
        @Parameter(description = "채팅방 ID", example = "1") @PathVariable("roomId") Long roomId,
        @Parameter(description = "페이징 정보") Pageable pageable
    );
}
