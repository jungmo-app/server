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
    public void handleLocationUpdate(LocationRequest meetingLocationDto) {
        Optional<Location> existingLocation = locationRepository.findByPlaceId(meetingLocationDto.getPlaceId());

        if (existingLocation.isPresent()) {
            updateLocation(meetingLocationDto); // 수정
        } else {
            createLocation(meetingLocationDto); // 생성
        }
    }

    @Transactional
    public Location updateLocation(LocationRequest locationRequest) {
        Location location = locationRepository.findByPlaceId(locationRequest.getPlaceId())
                .orElseThrow(() -> new BusinessException(ErrorCode.LOCATION_NOT_EXISTS));

        // 요청 데이터와 기존 데이터 비교 후 수정
        if (!(location.getLatitude() ==(locationRequest.getLatitude())) ||
                !(location.getLongitude() ==(locationRequest.getLongitude())) ||
                !location.getName().equals(locationRequest.getName()) ||
                !location.getRoadAddress().equals(locationRequest.getRoadAddress()) ||
                !location.getCategory().equals(locationRequest.getCategory())) {

            location.update(locationRequest);
            return locationRepository.save(location); // 수정 후 저장
        }

        return location; // 데이터가 같으면 기존 Location 반환
    }

    @Transactional
    public Location createLocation(LocationRequest dto) {
        // 새 Location 생성 및 저장
        Location location = Location.builder()
                .name(dto.getName())
                .roadAddress(dto.getRoadAddress())
                .latitude(dto.getLatitude())
                .longitude(dto.getLongitude())
                .placeId(dto.getPlaceId())
                .category(dto.getCategory())
                .build();
        return locationRepository.save(location);
    }


}
