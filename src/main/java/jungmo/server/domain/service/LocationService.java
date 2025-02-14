package jungmo.server.domain.service;

import jungmo.server.domain.dto.request.LocationRequest;
import jungmo.server.domain.entity.Location;
import jungmo.server.domain.repository.LocationRepository;
import jungmo.server.global.error.ErrorCode;
import jungmo.server.global.error.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LocationService {

    private final LocationRepository locationRepository;

    @Transactional
    public Location handleLocationCreation(LocationRequest dto) {
        // placeId로 Location이 존재하는지 확인
        Optional<Location> existingLocation = locationRepository.findByPlaceId(dto.getPlaceId());
        // 반환 또는 생성
        return existingLocation.orElseGet(() -> createLocation(dto));
    }
    @Transactional
    public Location createLocation(LocationRequest dto) {
        // 새 Location 생성 및 저장
        Location location = Location.builder()
                .placeId(dto.getPlaceId())
                .build();
        return locationRepository.save(location);
    }


}
