package jungmo.server.domain.service;

import jungmo.server.domain.dto.request.LocationRequestDto;
import jungmo.server.domain.entity.Gathering;
import jungmo.server.domain.entity.GatheringLocation;
import jungmo.server.domain.entity.Location;
import jungmo.server.domain.repository.GatheringLocationRepository;
import jungmo.server.domain.repository.LocationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class GatheringLocationService {

    private final GatheringService gatheringService;
    private final GatheringLocationRepository gatheringLocationRepository;
    private final LocationService locationService;

    @Transactional
    public void saveGatheringLocation(Long gatheringId, LocationRequestDto dto) {
        Gathering gathering = gatheringService.findGathering(gatheringId);
        Location location = locationService.findOrSaveLocation(dto);
        GatheringLocation gatheringLocation = new GatheringLocation();
        gatheringLocation.setGathering(gathering);
        gatheringLocation.setLocation(location);
        gatheringLocation.setFirstLocation(false);
        gatheringLocationRepository.save(gatheringLocation);
    }

    public void deleteGatheringLocation(Long gatheringId, Long gatheringLocationId) {

    }

}
