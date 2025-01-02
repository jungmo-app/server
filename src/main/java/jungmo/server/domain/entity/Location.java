package jungmo.server.domain.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Entity
@Table(name = "location")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Location {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "location_id")
    private Long id;
    @Column(name = "location_name")
    private String name;
    @Column(name = "location_road_address")
    private String roadAddress;
    @Column(name = "location_latitude")
    private double latitude;
    @Column(name = "location_longtitude")
    private double longitude;
    @Column(name = "location_place_id")
    private String placeId;
    @Column(name = "location_category")
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
}
