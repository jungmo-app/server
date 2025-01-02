package jungmo.server.domain.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "gathering_location")
@NoArgsConstructor
@AllArgsConstructor
public class GatheringLocation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "is_first_location")
    private boolean isFirstLocation;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gathering_id")
    private Gathering gathering;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gathering_location_id")
    private Location location;

    public void setFirstLocation(boolean firstLocation) {
        isFirstLocation = firstLocation;
    }

    public void setGathering(Gathering gathering) {
        this.gathering = gathering;
        gathering.getGatheringLocations().add(this);
    }

    public void removeGathering(Gathering gathering) {
        if (this.gathering != null) {
            this.gathering.getGatheringLocations().remove(this);
            this.gathering = null;
        }
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public void removeLocation(Location location) {
        if (this.location != null) {
            this.location = null;
        }
    }


}
