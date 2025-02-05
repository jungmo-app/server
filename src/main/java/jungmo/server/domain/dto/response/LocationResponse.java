package jungmo.server.domain.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class LocationResponse {

    private Long id;
    private String name;
    private String roadAddress;
    private double latitude;
    private double longitude;
    private String category;
    @JsonIgnore
    private boolean isFirst;

    public LocationResponse(Long id, String name, String roadAddress, double latitude, double longitude, String category, boolean isFirst) {
        this.id = id;
        this.name = name;
        this.roadAddress = roadAddress;
        this.latitude = latitude;
        this.longitude = longitude;
        this.category = category;
        this.isFirst = isFirst;
    }
}
