package jungmo.server.domain.provider;

import jungmo.server.domain.entity.Gathering;
import jungmo.server.domain.repository.GatheringRepository;
import jungmo.server.global.error.ErrorCode;
import jungmo.server.global.error.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GatheringDataProvider {

    private final GatheringRepository gatheringRepository;

    public Gathering findGathering(Long gatheringId) {
        Gathering gathering =  gatheringRepository.findById(gatheringId)
                .orElseThrow(() -> new BusinessException(ErrorCode.GATHERING_NOT_EXISTS));
        if (gathering.getIsDeleted()) {
            throw new BusinessException(ErrorCode.GATHERING_ALREADY_DELETED);
        }
        return gathering;
    }
}
