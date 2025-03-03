package jungmo.server.domain.repository;

import jungmo.server.domain.dto.response.GatheringListResponse;

import java.time.LocalDate;
import java.util.List;

public interface GatheringRepositoryCustom {
    List<GatheringListResponse> findAllByUserIdAndDate(Long userId, LocalDate currentDate);
}
