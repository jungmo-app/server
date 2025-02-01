package jungmo.server.domain.dto.request;

import jungmo.server.domain.entity.Authority;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class GatheringUserRequest {
    private Authority authority;
    public GatheringUserRequest(Authority authority) {
        this.authority = authority;
    }
}
