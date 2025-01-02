package jungmo.server.domain.dto.response;

import lombok.Data;

@Data
public class LocationResponseDto {

    private Long id;
    private String name;
    private String roadAddress;
    private double latitude;
    private double longitude;
    private String placeId;
    private String category;
    private boolean isFirst;

    public LocationResponseDto(Long id, String name, String roadAddress, double latitude, double longitude, String placeId, String category, boolean isFirst) {
        this.id = id;
        this.name = name;
        this.roadAddress = roadAddress;
        this.latitude = latitude;
        this.longitude = longitude;
        this.placeId = placeId;
        this.category = category;
        this.isFirst = isFirst;
    }
}
