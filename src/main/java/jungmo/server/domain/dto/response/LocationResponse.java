package jungmo.server.domain.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class LocationResponse {

    private Long id;
    private String placeId;
    @JsonIgnore
    private boolean isFirst;

    public LocationResponse(Long id, String placeId, boolean isFirst) {
        this.id = id;
        this.placeId = placeId;
        this.isFirst = isFirst;
    }
}
