package jungmo.server.domain.service;

import jungmo.server.domain.dto.request.LocationRequestDto;
import jungmo.server.domain.entity.*;
import jungmo.server.domain.repository.GatheringLocationRepository;
import jungmo.server.domain.repository.GatheringUserRepository;
import jungmo.server.domain.repository.LocationRepository;
import jungmo.server.domain.repository.UserRepository;
import jungmo.server.global.auth.dto.response.SecurityUserDto;
import jungmo.server.global.auth.service.PrincipalDetails;
import jungmo.server.global.error.ErrorCode;
import jungmo.server.global.error.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;


@Service
@RequiredArgsConstructor
public class GatheringLocationService {

    private final GatheringService gatheringService;
    private final GatheringLocationRepository gatheringLocationRepository;
    private final LocationService locationService;
    private final GatheringUserRepository gatheringUserRepository;
    private final UserRepository userRepository;

    @Transactional
    public void saveGatheringLocation(Long gatheringId, LocationRequestDto dto) {
        User user = getUser();
        Gathering gathering = gatheringService.findGathering(gatheringId);
        validateWriteAuthority(user, gathering);
        Location location = locationService.findOrSaveLocation(dto);
        GatheringLocation gatheringLocation = new GatheringLocation();
        gatheringLocation.setGathering(gathering);
        gatheringLocation.setLocation(location);
        gatheringLocation.setFirstLocation(false);
        gatheringLocationRepository.save(gatheringLocation);
    }

    @Transactional
    public void deleteGatheringLocation(Long gatheringId, Long gatheringLocationId) {
        Gathering gathering = gatheringService.findGathering(gatheringId);
        GatheringLocation gatheringLocation = gatheringLocationRepository.findById(gatheringLocationId)
                .orElseThrow(() -> new BusinessException(ErrorCode.GATHERING_LOCATION_NOT_EXISTS));
        User user = getUser();
        // 로그인 된 사용자가 write권한을 가지고있는지 검증
        validateWriteAuthority(user, gathering);

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
