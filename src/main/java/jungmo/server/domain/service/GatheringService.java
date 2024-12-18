package jungmo.server.domain.service;

import jungmo.server.domain.dto.request.GatheringDto;
import jungmo.server.domain.dto.request.GatheringUserDto;
import jungmo.server.domain.entity.*;
import jungmo.server.domain.repository.GatheringRepository;
import jungmo.server.domain.repository.UserRepository;
import jungmo.server.global.auth.dto.response.SecurityUserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GatheringService {

    private final GatheringRepository gatheringRepository;
    private final UserRepository userRepository;
    private final GatheringUserService gatheringUserService;

    @Transactional
    public Long saveGathering(GatheringDto dto) {
        //모임 생성
        Gathering gathering = Gathering.builder()
                .title(dto.getTitle())
                .startDate(dto.getStartDate())
                .endDate(dto.getEndDate())
                .startTime(dto.getStartTime())
                .memo(dto.getMemo())
                .allExpense(0L)
                .build();
        //모임유저 생성
        GatheringUserDto writeUser = new GatheringUserDto(Authority.WRITE, GatheringStatus.ACCEPT);
        GatheringUser gatheringUser = gatheringUserService.saveGatheringUser(writeUser);
        //연관관계 매핑
        gatheringUser.setGathering(gathering);
        Gathering savedGathering = gatheringRepository.save(gathering);
        return savedGathering.getId();
    }

    @Transactional
    public void updateGathering(Long gatheringId) {

    }

    @Transactional
    public void deleteGathering(Long gatheringId) {

    }

    public void getGathering() {

    }

    public void getGatherings() {

    }

    private User getUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        SecurityUserDto securityUser = (SecurityUserDto) authentication.getPrincipal();
        Long userId = securityUser.getUserId();
        return userRepository.findById(userId).orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));
    }
}
