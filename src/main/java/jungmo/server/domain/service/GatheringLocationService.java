package jungmo.server.domain.service;

import jungmo.server.domain.dto.request.LocationRequest;
import jungmo.server.domain.entity.*;
import jungmo.server.domain.provider.GatheringDataProvider;
import jungmo.server.domain.provider.GatheringLocationDataProvider;
import jungmo.server.domain.provider.UserDataProvider;
import jungmo.server.domain.repository.GatheringLocationRepository;
import jungmo.server.domain.repository.GatheringUserRepository;
import jungmo.server.domain.service.policy.GatheringLocationPolicy;
import jungmo.server.global.aop.annotation.CheckWritePermission;
import jungmo.server.global.error.ErrorCode;
import jungmo.server.global.error.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Slf4j
@Service
@RequiredArgsConstructor
public class GatheringLocationService {

    private final GatheringLocationRepository gatheringLocationRepository;
    private final LocationService locationService;
    private final GatheringDataProvider gatheringDataProvider;
    private final GatheringLocationDataProvider gatheringLocationDataProvider;
    private final GatheringLocationPolicy gatheringLocationPolicy;

    @CheckWritePermission
    @Transactional
    public GatheringLocation saveGatheringLocation(Long gatheringId, LocationRequest dto, boolean isFirst) {
        //모임에 포함된 장소 중복 있는지 검증.
        gatheringLocationPolicy.isPlaceAlreadyExists(gatheringId, dto.getPlaceId());

        Gathering gathering = gatheringDataProvider.findGathering(gatheringId);

        Location location = locationService.handleLocationCreation(dto);
        GatheringLocation gatheringLocation = new GatheringLocation();
        gatheringLocation.setGathering(gathering);
        gatheringLocation.setLocation(location);
        gatheringLocation.setFirstLocation(isFirst);
        return gatheringLocationRepository.save(gatheringLocation);
    }

    @CheckWritePermission
    @Transactional
    public void deleteGatheringLocation(Long gatheringId, Long gatheringLocationId) {
        Gathering gathering = gatheringDataProvider.findGathering(gatheringId);
        GatheringLocation gatheringLocation = gatheringLocationDataProvider.findGatheringLocation(gatheringLocationId);

        log.info("GatheringLocation ID: {}", gatheringLocationId);
        log.info("Gathering: {}", gatheringLocation.getGathering());
        log.info("Is initialized: {}", Hibernate.isInitialized(gatheringLocation.getGathering()));


        // gatheringId와 gatheringLocation의 관계 검증
        if (!gatheringLocation.getGathering().getId().equals(gatheringId)) {
            throw new BusinessException(ErrorCode.INVALID_RELATION);
        }
        // 연관관계 해제
        gatheringLocation.removeGathering(gathering);
        gatheringLocation.removeLocation(gatheringLocation.getLocation());
        // 삭제
        gatheringLocationRepository.delete(gatheringLocation);
    }

}
