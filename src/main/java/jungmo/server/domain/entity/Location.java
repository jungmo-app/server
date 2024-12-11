package jungmo.server.domain.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "location")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
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

}
