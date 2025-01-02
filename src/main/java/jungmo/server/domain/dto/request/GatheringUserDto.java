package jungmo.server.domain.dto.request;

import jungmo.server.domain.entity.Authority;
import lombok.Data;

@Data
public class GatheringUserDto {
    private Authority authority;
    public GatheringUserDto(Authority authority) {
        this.authority = authority;
    }
}
