package jungmo.server.domain.provider;

import jungmo.server.domain.entity.GatheringLocation;
import jungmo.server.domain.repository.GatheringLocationRepository;
import jungmo.server.global.error.ErrorCode;
import jungmo.server.global.error.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class GatheringLocationDataProvider {

    private final GatheringLocationRepository gatheringLocationRepository;

    public GatheringLocation findGatheringLocation(Long gatheringLocationId) {
        return gatheringLocationRepository.findById(gatheringLocationId)
                .orElseThrow(() -> new BusinessException(ErrorCode.GATHERING_LOCATION_NOT_EXISTS));
    }

    public GatheringLocation findFirstLocation(Long gatheringId) {
        return gatheringLocationRepository.findByFirstLocationAndGatheringId(gatheringId)
                .orElseThrow(() -> new BusinessException(ErrorCode.GATHERING_LOCATION_NOT_EXISTS));
    }


}
