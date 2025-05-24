package jungmo.server.domain.service.chat;

import java.util.Objects;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatRedisService {

    private final RedisTemplate<String,Object> redisTemplate;

    private static final String ONLINE = "OnlineUser:";
    private static final String SESSION_TO_USER = "SessionToUser:";
    private static final String SUBSCRIBED_ROOM = "SubscribedRoom:";

    public void registerSession(String sessionId, Long userId) {
        redisTemplate.opsForSet().add(ONLINE + userId, sessionId);
        redisTemplate.opsForValue().set(SESSION_TO_USER + sessionId, userId);

        log.info("사용자 ID [{}]가 접속하였습니다. 세션 ID : [{}]", userId, sessionId);
    }

    public void unregisterSession(String sessionId) {
        Long userId = (Long) redisTemplate.opsForValue().get(SESSION_TO_USER + sessionId);
        if (Objects.nonNull(userId)) {
            redisTemplate.opsForSet().remove(ONLINE + userId, sessionId);
            redisTemplate.delete(SESSION_TO_USER + sessionId);

            log.info("사용자 ID [{}]가 퇴장하였습니다. 세션 ID : [{}]", userId, sessionId);
        }
    }

    public void subscribeRoom(Long roomId, String sessionId) {
        redisTemplate.opsForSet().add(SUBSCRIBED_ROOM + roomId, sessionId);

        log.info("[{}]의 채팅방에 세션 ID [{}]가 입장하였습니다.", roomId, sessionId);
    }

    public void unsubscribeRoom(Long roomId, String sessionId) {
        redisTemplate.opsForSet().remove(SUBSCRIBED_ROOM + roomId, sessionId);

        log.info("[{}]의 채팅방에 세션 ID [{}]가 퇴장하였습니다.", roomId, sessionId);
    }

    /**
     * STOMP 온라인 여부 조회
     * @param userId - 유저 고유 아이디
     * @return true / false
     */
    public boolean isOnlineUser(Long userId) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(ONLINE + userId));
    }

    /**
     * 유저의 Session Id 정보 조회
     * @param userId - 유저 고유 아이디
     * @return 접속한 웹 고유 세션 아이디 목록
     */
    public Set<Object> getOnlineUserSessions(Long userId) {
        return redisTemplate.opsForSet().members(ONLINE + userId);
    }

    /**
     * Session ID 를 이용하여 userId 조회
     * @param sessionId - 접속한 웹 고유 sessionId
     * @return userId
     */
    public Long getUserIdBySessionId(String sessionId) {
        return (Long) redisTemplate.opsForValue().get(ONLINE + sessionId);
    }

}
