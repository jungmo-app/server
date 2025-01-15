package jungmo.server.domain.dto.request;

import jungmo.server.domain.entity.Authority;
import lombok.Data;

@Data
public class GatheringUserRequest {
    private Authority authority;
    public GatheringUserRequest(Authority authority) {
        this.authority = authority;
    }
}
