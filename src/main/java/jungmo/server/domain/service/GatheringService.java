package jungmo.server.domain.service;

import jungmo.server.domain.dto.request.GatheringRequest;
import jungmo.server.domain.dto.request.GatheringUserRequest;
import jungmo.server.domain.dto.response.GatheringListResponse;
import jungmo.server.domain.dto.response.GatheringResponse;
import jungmo.server.domain.dto.response.LocationResponse;
import jungmo.server.domain.dto.response.UserResponse;
import jungmo.server.domain.entity.*;
import jungmo.server.domain.provider.GatheringDataProvider;
import jungmo.server.domain.provider.GatheringLocationDataProvider;
import jungmo.server.domain.provider.UserDataProvider;
import jungmo.server.domain.repository.GatheringLocationRepository;
import jungmo.server.domain.repository.GatheringRepository;
import jungmo.server.domain.repository.GatheringUserRepository;
import jungmo.server.global.aop.annotation.CheckWritePermission;
import jungmo.server.global.error.ErrorCode;
import jungmo.server.global.error.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
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
    private final GatheringUserRepository gatheringUserRepository;
    private final GatheringUserService gatheringUserService;
    private final GatheringLocationService gatheringLocationService;
    private final UserDataProvider userDataProvider;
    private final GatheringDataProvider gatheringDataProvider;
    private final GatheringLocationDataProvider gatheringLocationDataProvider;
    private final GatheringLocationRepository gatheringLocationRepository;

    @Transactional
    public Long saveGathering(GatheringRequest dto) {
        //모임 생성
        Gathering gathering = Gathering.builder()
                .title(dto.getTitle())
                .startDate(dto.getStartDate())
                .endDate(dto.getEndDate())
                .startTime(dto.getStartTime())
                .memo(dto.getMemo())
                .isDeleted(false)
                .build();
        Gathering savedGathering = gatheringRepository.save(gathering);
        //모임을 만든사람과 매핑
        GatheringUserRequest gatheringUserDto = new GatheringUserRequest(Authority.WRITE);
        User user = userDataProvider.getUser();
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

    @CheckWritePermission
    @Transactional
    public void updateGathering(Long gatheringId, GatheringRequest gatheringDto) {
        Gathering gathering = gatheringDataProvider.findGathering(gatheringId);

        if (!gathering.getIsDeleted()) {
            gathering.update(gatheringDto);
            gatheringUserService.updateGatheringUsers(gatheringId,gatheringDto.getUserIds());
            GatheringLocation firstLocation = gatheringLocationDataProvider.findFirstLocation(gatheringId);
            //만나는 장소 업데이트
            gatheringLocationService.deleteGatheringLocation(gatheringId, firstLocation.getId());
            gatheringLocationService.saveGatheringLocation(gatheringId, gatheringDto.getMeetingLocation(), true);

        } else {
            throw new BusinessException(ErrorCode.GATHERING_ALREADY_DELETED);
        }
    }

    @CheckWritePermission
    @Transactional
    public void deleteGathering(Long gatheringId) {
        Gathering gathering = gatheringDataProvider.findGathering(gatheringId);

        if (!gathering.getIsDeleted()) {
            gathering.setDeleted(true);
        } else {
            throw new BusinessException(ErrorCode.GATHERING_ALREADY_DELETED);
        }
    }

    @Transactional(readOnly = true)
    public GatheringResponse findGathering(Long gatheringId) {
        Gathering gathering = gatheringDataProvider.findGathering(gatheringId);
        GatheringResponse dto = toDto(gathering);
        User user = userDataProvider.getUser();

         if (user == null) {
         dto.setAuthority(Authority.READ);
         return dto;
         }

         boolean hasWriteAuthority = gatheringUserRepository
         .findByAuthority(user, gathering, Authority.WRITE)
         .isPresent();

         dto.setAuthority(hasWriteAuthority ? Authority.WRITE : Authority.READ);
        return dto;
    }

    @Transactional(readOnly = true)
    public List<GatheringListResponse> findMyGatherings(LocalDate currentDate) {
        User user = userDataProvider.getUser();
        List<GatheringListResponse> allGatherings = gatheringRepository.findAllByUserIdAndDate(user.getId(),currentDate);
        return allGatherings;
    }

    @Transactional(readOnly = true)
    public GatheringResponse toDto(Gathering gathering) {
        GatheringResponse dto = gathering.toDto();
        // 모임 참석자 정보 가져오기
        List<UserResponse> gatheringUsers = gatheringUserRepository.findAllBy(gathering.getId());
        dto.setGatheringUsers(gatheringUsers);

        // 모임 장소 정보 가져오기
        List<LocationResponse> locations = gatheringLocationRepository.findAllByGatheringId(gathering.getId());

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

}
