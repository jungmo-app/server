package jungmo.server.domain.service.policy;

import jungmo.server.domain.repository.GatheringLocationRepository;
import jungmo.server.global.error.ErrorCode;
import jungmo.server.global.error.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GatheringLocationPolicy {

    private final GatheringLocationRepository gatheringLocationRepository;

    public void isPlaceAlreadyExists(Long gatheringId, String placeId) {
        if(gatheringLocationRepository.isPlaceAlreadyExists(gatheringId, placeId)) {
            throw new BusinessException(ErrorCode.LOCATION_ALREADY_EXISTS);
        }
    }
}
