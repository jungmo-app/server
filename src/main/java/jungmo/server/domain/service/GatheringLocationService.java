package jungmo.server.domain.service;

import jungmo.server.domain.dto.request.LocationRequest;
import jungmo.server.domain.dto.response.LocationResponse;
import jungmo.server.domain.entity.*;
import jungmo.server.domain.repository.GatheringLocationRepository;
import jungmo.server.domain.repository.GatheringRepository;
import jungmo.server.domain.repository.GatheringUserRepository;
import jungmo.server.domain.repository.UserRepository;
import jungmo.server.global.auth.dto.response.SecurityUserDto;
import jungmo.server.global.auth.service.PrincipalDetails;
import jungmo.server.global.error.ErrorCode;
import jungmo.server.global.error.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Hibernate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class GatheringLocationService {

    private final GatheringRepository gatheringRepository;
    private final GatheringLocationRepository gatheringLocationRepository;
    private final LocationService locationService;
    private final GatheringUserRepository gatheringUserRepository;
    private final UserRepository userRepository;

    @Transactional
    public GatheringLocation saveGatheringLocation(Long gatheringId, LocationRequest dto, boolean isFirst) {
        User user = getUser();
        Gathering gathering = gatheringRepository.findById(gatheringId)
                .orElseThrow(() -> new BusinessException(ErrorCode.GATHERING_NOT_EXISTS));
        validateWriteAuthority(user, gathering);
        Location location = locationService.handleLocationCreation(dto);
        GatheringLocation gatheringLocation = new GatheringLocation();
        gatheringLocation.setGathering(gathering);
        gatheringLocation.setLocation(location);
        gatheringLocation.setFirstLocation(isFirst);
        return gatheringLocationRepository.save(gatheringLocation);
    }

    @Transactional
    public void deleteGatheringLocation(Long gatheringId, Long gatheringLocationId) {
        Gathering gathering = gatheringRepository.findById(gatheringId)
                .orElseThrow(() -> new BusinessException(ErrorCode.GATHERING_NOT_EXISTS));
        GatheringLocation gatheringLocation = gatheringLocationRepository.findById(gatheringLocationId)
                .orElseThrow(() -> new BusinessException(ErrorCode.GATHERING_LOCATION_NOT_EXISTS));
        User user = getUser();
        // 로그인 된 사용자가 write권한을 가지고있는지 검증
        validateWriteAuthority(user, gathering);

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

    @Transactional(readOnly = true)
    public List<LocationResponse> findAllGatheringLocations(Long gatheringId) {
        return gatheringLocationRepository.findAllByGatheringId(gatheringId);
    }

    @Transactional(readOnly = true)
    public GatheringLocation findFirstLocation(Long gatheringId) {
        return gatheringLocationRepository.findByFirstLocationAndGatheringId(gatheringId)
                .orElseThrow(() -> new BusinessException(ErrorCode.GATHERING_LOCATION_NOT_EXISTS));
    }

    private void validateWriteAuthority(User user, Gathering gathering) {
        gatheringUserRepository.findByAuthority(user, gathering, Authority.WRITE)
                .orElseThrow(() -> new BusinessException(ErrorCode.NO_AUTHORITY));
    }

    private User getUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
        SecurityUserDto securityUser = SecurityUserDto.from(principalDetails);
        Long userId = securityUser.getUserId();

        // 데이터베이스에서 사용자 조회
        return userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));
    }
}
