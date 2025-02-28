package jungmo.server.domain.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jungmo.server.domain.dto.request.NotificationRequest;
import jungmo.server.domain.dto.response.NotificationResponse;
import jungmo.server.global.result.ResultDetailResponse;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

@Tag(name = "SSE 알림", description = "알림 관련 API")
public interface SseSwaggerController {

    @Operation(summary = "알림 이벤트 구독", description = "클라이언트의 이벤트 구독을 수락하는 초기 SSE 연결 요청으로 연결을 함으로써 서버에서 클라이언트로 이벤트를 보낼 수 있게 된다.")
    public SseEmitter subscribe();

    @Operation(summary = "알림 내역 조회", description = "로그인 한 사용자가 수신한 알림 내역을 조회한다.")
    public List<NotificationResponse> getNotifications();

    @Operation(summary = "알림 읽음 처리", description = "알림창을 열때 안읽은 알림들을 읽음 처리한다.")
    public ResultDetailResponse<Void> markNotificationsAsRead(@RequestBody NotificationRequest request);

    @Operation(summary = "알림 삭제 처리", description = "특정 알림들을 삭제한다.")
    public ResultDetailResponse<Void> deleteNotifications(@RequestBody NotificationRequest request);
}
