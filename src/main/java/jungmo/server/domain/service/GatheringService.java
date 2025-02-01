package jungmo.server.domain.service;

import jungmo.server.domain.dto.request.GatheringRequest;
import jungmo.server.domain.dto.request.GatheringUserRequest;
import jungmo.server.domain.dto.response.GatheringListResponse;
import jungmo.server.domain.dto.response.GatheringResponse;
import jungmo.server.domain.dto.response.LocationResponse;
import jungmo.server.domain.dto.response.UserResponse;
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

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class GatheringService {

    private final GatheringRepository gatheringRepository;
    private final UserRepository userRepository;
    private final GatheringUserRepository gatheringUserRepository;
    private final GatheringUserService gatheringUserService;
    private final GatheringLocationService gatheringLocationService;
    private final LocationService locationService;

    @Transactional
    public Long saveGathering(GatheringRequest dto) {
        //모임 생성
        Gathering gathering = Gathering.builder()
                .title(dto.getTitle())
                .startDate(dto.getStartDate())
                .endDate(dto.getEndDate())
                .startTime(dto.getStartTime())
                .memo(dto.getMemo())
                .allExpense(0L)
                .isDeleted(false)
                .build();
        Gathering savedGathering = gatheringRepository.save(gathering);
        //모임을 만든사람과 매핑
        GatheringUserRequest gatheringUserDto = new GatheringUserRequest(Authority.WRITE);
        User user = getUser();
        gatheringUserService.saveGatheringUser(user.getId(),gatheringUserDto,savedGathering);
        //초대 된 사람들과의 매핑
        Set<Long> userIds = new HashSet<>(dto.getUserIds());
        gatheringUserService.addUsersToGathering(savedGathering, userIds);
        // 만나는 장소와의 매핑, 저장
        GatheringLocation gatheringLocation = gatheringLocationService.saveGatheringLocation(gathering.getId(), dto.getMeetingLocation(), true);

        log.info("GatheringLocation ID: {}", gatheringLocation.getId());
        log.info("Gathering: {}", gatheringLocation.getGathering());
        log.info("Is initialized: {}", Hibernate.isInitialized(gatheringLocation.getGathering()));
        return savedGathering.getId();
    }

    @Transactional
    public void updateGathering(Long gatheringId, GatheringRequest gatheringDto) {
        User user = getUser();
        Gathering gathering = gatheringRepository.findById(gatheringId).orElseThrow(() -> new BusinessException(ErrorCode.GATHERING_NOT_EXISTS));
        Optional<GatheringUser> gatheringUser = gatheringUserRepository.findByAuthority(user, gathering, Authority.WRITE);
        if (gatheringUser.isPresent()) {
            if (!gathering.getIsDeleted()) {
                gathering.update(gatheringDto);
                gatheringUserService.updateGatheringUsers(gatheringId,gatheringDto.getUserIds());
                GatheringLocation firstLocation = gatheringLocationService.findFirstLocation(gatheringId);
                //만나는 장소 업데이트
                gatheringLocationService.deleteGatheringLocation(gatheringId, firstLocation.getId());

                locationService.handleLocationUpdate(gatheringDto.getMeetingLocation());
                gatheringLocationService.saveGatheringLocation(gatheringId, gatheringDto.getMeetingLocation(), true);

            } else {
                throw new BusinessException(ErrorCode.GATHERING_ALREADY_DELETED);
            }
        } else {
            throw new BusinessException(ErrorCode.NOT_HAVE_WRITE_AUTHORITY);
        }
    }

    @Transactional
    public void deleteGathering(Long gatheringId) {
        Gathering gathering = gatheringRepository.findById(gatheringId).orElseThrow(() -> new BusinessException(ErrorCode.GATHERING_NOT_EXISTS));
        User user = getUser();
        Optional<GatheringUser> gatheringUser = gatheringUserRepository.findByAuthority(user, gathering, Authority.WRITE);
        if (gatheringUser.isPresent()) {
            if (!gathering.getIsDeleted()) {
                gathering.setDeleted(true);
            } else {
                throw new BusinessException(ErrorCode.GATHERING_ALREADY_DELETED);
            }
        } else {
            throw new BusinessException(ErrorCode.NOT_HAVE_WRITE_AUTHORITY);
        }
    }

    @Transactional(readOnly = true)
    public Gathering findGathering(Long gatheringId) {
        Gathering gathering = gatheringRepository.findById(gatheringId).orElseThrow(() -> new BusinessException(ErrorCode.GATHERING_NOT_EXISTS));
        if (gathering.getIsDeleted()) {
            throw new BusinessException(ErrorCode.GATHERING_ALREADY_DELETED);
        }
        return gathering;
    }

    @Transactional(readOnly = true)
    public List<GatheringListResponse> findMyGatherings() {
        User user = getUser();
        List<GatheringListResponse> allGatherings = gatheringRepository.findAllByUserId(user.getId());
        return allGatherings;
    }

    @Transactional(readOnly = true)
    public GatheringResponse toDto(Gathering gathering) {
        GatheringResponse dto = gathering.toDto();
        // 모임 참석자 정보 가져오기
        List<UserResponse> gatheringUsers = gatheringUserService.getGatheringUsers(gathering.getId());
        dto.setGatheringUsers(gatheringUsers);

        // 모임 장소 정보 가져오기
        List<LocationResponse> locations = gatheringLocationService.findAllGatheringLocations(gathering.getId());

        // 만나기로 한 장소와 나머지 장소 분리
        LocationResponse meetingPlace = locations.stream()
                .filter(LocationResponse::isFirst) // isFirstLocation == true 인 장소 필터링
                .findFirst() // 첫 번째 장소만 가져옴
                .orElse(null); // 없으면 null

        List<LocationResponse> places = locations.stream()
                .filter(location -> !location.isFirst()) // isFirstLocation == false 인 장소 필터링
                .collect(Collectors.toList());

        // DTO에 값 설정
        dto.setMeetingLocation(meetingPlace);
        dto.setLocations(places);

        return dto;
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
