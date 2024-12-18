package jungmo.server.domain.dto.request;

import jungmo.server.domain.entity.Authority;
import jungmo.server.domain.entity.GatheringStatus;
import lombok.Data;

@Data
public class GatheringUserDto {
    private Authority authority;
    private GatheringStatus status;

    public GatheringUserDto(Authority authority, GatheringStatus status) {
        this.authority = authority;
        this.status = status;
    }
}
