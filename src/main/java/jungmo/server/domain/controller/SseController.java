package jungmo.server.domain.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jungmo.server.domain.dto.response.EventPayload;
import jungmo.server.domain.dto.response.NotificationResponse;
import jungmo.server.domain.service.NotificationService;
import jungmo.server.domain.service.SseEmitterService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

@RestController
@RequestMapping("/sse")
@RequiredArgsConstructor
public class SseController implements SseSwaggerController{

    private final SseEmitterService sseEmitterService;
    private final NotificationService notificationService;

    @Override
    @GetMapping(value = "/subscribe", produces = "text/event-stream")
    public SseEmitter subscribe() {
        return sseEmitterService.subscribe();
    }

    @Override
    @GetMapping("/notifications")
    @Operation(summary = "알림 내역 조회", description = "로그인 한 사용자가 수신한 알림 내역을 조회한다.")
    public List<NotificationResponse> getNotifications() {
        return notificationService.getNotifications();
    }
}