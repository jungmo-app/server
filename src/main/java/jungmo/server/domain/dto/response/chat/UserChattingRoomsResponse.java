package jungmo.server.domain.dto.response.chat;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserChattingRoomsResponse {

    private Long userId;
    private List<Long> chattingRoomIds;

}
