package jungmo.server.domain.service;

import jungmo.server.domain.dto.request.GatheringUserRequest;
import jungmo.server.domain.dto.response.UserResponse;
import jungmo.server.domain.entity.*;
import jungmo.server.domain.provider.GatheringDataProvider;
import jungmo.server.domain.provider.UserDataProvider;
import jungmo.server.domain.repository.GatheringRepository;
import jungmo.server.domain.repository.GatheringUserRepository;
import jungmo.server.domain.repository.UserRepository;
import jungmo.server.domain.service.policy.GatheringUserPolicy;
import jungmo.server.global.auth.dto.response.SecurityUserDto;
import jungmo.server.global.auth.service.PrincipalDetails;
import jungmo.server.global.error.ErrorCode;
import jungmo.server.global.error.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
public class GatheringUserService {

    private final GatheringUserRepository gatheringUserRepository;
    private final UserRepository userRepository;
    private final UserDataProvider userDataProvider;
    private final GatheringDataProvider gatheringDataProvider;
    private final GatheringUserPolicy gatheringUserPolicy;
    private final GatheringNotificationService gatheringNotificationService;

    /**
     * 모임 참석자 초대하는 로직
     * @param gatheringId
     * @param userIds
     */
    @Transactional
    public void updateGatheringUsers(Long gatheringId,List<Long> userIds) {
        Gathering gathering = gatheringDataProvider.findGathering(gatheringId);
        User writeUser = userDataProvider.getUser();

        // 초대 할 대상자 조회
        List<User> usersToUpdate = userRepository.findAllById(userIds);
        gatheringUserPolicy.validateUsers(usersToUpdate, userIds);

        // 추가 또는 삭제 대상 검증
        List<GatheringUser> existingGatheringUsers = gatheringUserRepository.findByGatheringId(gatheringId);
        Set<Long> existingUserIds = existingGatheringUsers.stream()
                .map(gu -> gu.getUser().getId())
                .collect(Collectors.toSet());

        // 추가 대상
        Set<Long> newUserIds = new HashSet<>(userIds);
        newUserIds.removeAll(existingUserIds);

        // 삭제 대상
        Set<Long> removedUserIds = new HashSet<>(existingUserIds);  //현재 모임에 존재하는 유저들
        removedUserIds.removeAll(userIds);  //업데이트 될 유저들 중 현재 존재하는 유저들 제외
        removedUserIds.remove(writeUser.getId()); //현재 모임을 수정하는 유저 제외


        Set<Long> remainingUserIds = new HashSet<>(existingUserIds);
        remainingUserIds.removeAll(removedUserIds);
        gatheringNotificationService.update(remainingUserIds, gatheringId);

        removeUsersFromGathering(existingGatheringUsers, removedUserIds);
        addUsersToGathering(gathering, newUserIds);
    }

    @Transactional
    public void addUsersToGathering(Gathering gathering, Set<Long> newUserIds) {
        List<User> usersToInvite = userRepository.findAllById(newUserIds);
        User currentUser = userDataProvider.getUser();

        Set<Long> foundUserIds = usersToInvite.stream()
                .map(User::getId)
                .collect(Collectors.toSet());

        gatheringUserPolicy.validateNewUsers(currentUser.getId(), foundUserIds, newUserIds);

        List<GatheringUser> newGatheringUsers = usersToInvite.stream()
                .map(user -> GatheringUser.builder()
                        .authority(Authority.READ)
                        .build()
                        .setUser(user)
                        .setGathering(gathering))
                .toList();;

        gatheringUserRepository.saveAll(newGatheringUsers);
        gatheringNotificationService.invite(newUserIds, gathering.getId());

    }

    @Transactional
    public void removeUsersFromGathering(List<GatheringUser> existingGatheringUsers, Set<Long> removedUserIds) {
        List<GatheringUser> gatheringUsersToRemove = existingGatheringUsers.stream()
                .filter(gu -> removedUserIds.contains(gu.getUser().getId()))
                .toList();

        // 2. 연관 관계 해제
        gatheringUsersToRemove.forEach(gu -> {
            log.info("🗑 삭제 대상 GatheringUser ID: {}", gu.getId());
            gu.removeUser(gu.getUser()); // User와의 연관 관계 해제
            gu.removeGathering(gu.getGathering()); // Gathering과의 연관 관계 해제
        });
        gatheringUserRepository.deleteAll(gatheringUsersToRemove); //Batch 삭제
    }

    /**
     * 모임 참석자 생성 로직
     * @param userId
     * @param dto
     * @param gathering
     * @return
     */
    @Transactional
    public GatheringUser saveGatheringUser(Long userId, GatheringUserRequest dto, Gathering gathering) {
        //모임유저 생성
        User user = userDataProvider.findUserById(userId);
        GatheringUser gatheringUser = GatheringUser.builder()
                .authority(dto.getAuthority())
                .build();
        //연관관계 매핑
        gatheringUser.setUser(user);
        gatheringUser.setGathering(gathering);
        return gatheringUserRepository.save(gatheringUser);
    }

}
