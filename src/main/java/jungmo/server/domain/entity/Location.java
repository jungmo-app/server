package jungmo.server.domain.entity;

import jakarta.persistence.*;
import jungmo.server.domain.dto.request.LocationRequest;
import lombok.*;

@Getter
@Entity
@Table(name = "location")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Location {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "name")
    private String name;
    @Column(name = "road_address")
    private String roadAddress;
    @Column(name = "latitude")
    private double latitude;
    @Column(name = "longitude")
    private double longitude;
    @Column(name = "place_id")
    private String placeId;
    @Column(name = "category")
    private String category;

    @Builder
    public Location(Long id, String name, String roadAddress, double latitude, double longitude, String placeId, String category) {
        this.id = id;
        this.name = name;
        this.roadAddress = roadAddress;
        this.latitude = latitude;
        this.longitude = longitude;
        this.placeId = placeId;
        this.category = category;
    }

    public void update(LocationRequest request) {
        this.name = request.getName();
        this.roadAddress = request.getRoadAddress();
        this.latitude = request.getLatitude();
        this.longitude = request.getLongitude();
        this.placeId = request.getPlaceId();
        this.category = request.getCategory();
    }

}
