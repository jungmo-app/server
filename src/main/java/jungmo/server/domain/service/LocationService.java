package jungmo.server.domain.service;

import jungmo.server.domain.dto.request.LocationRequestDto;
import jungmo.server.domain.entity.Location;
import jungmo.server.domain.repository.LocationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LocationService {

    private final LocationRepository locationRepository;

    @Transactional
    public Location findOrSaveLocation(LocationRequestDto dto) {
        return locationRepository.findByPlaceId(dto.getPlaceId())
                .orElseGet(() -> {
                    Location location = Location.builder()
                            .name(dto.getName())
                            .roadAddress(dto.getRoadAddress())
                            .latitude(dto.getLatitude())
                            .longitude(dto.getLongitude())
                            .placeId(dto.getPlaceId())
                            .category(dto.getCategory())
                            .build();
                    return locationRepository.save(location);
                });
    }
}
