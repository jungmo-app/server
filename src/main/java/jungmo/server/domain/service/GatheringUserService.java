package jungmo.server.domain.service;

import jungmo.server.domain.dto.request.GatheringUserRequest;
import jungmo.server.domain.dto.response.UserResponse;
import jungmo.server.domain.entity.*;
import jungmo.server.domain.repository.GatheringRepository;
import jungmo.server.domain.repository.GatheringUserRepository;
import jungmo.server.domain.repository.UserRepository;
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
    private final GatheringRepository gatheringRepository;
    private final EmailService emailService;

    /**
     * 모임 참석자 초대하는 로직
     * @param gatheringId
     * @param userIds
     */
    @Transactional
    public void updateGatheringUsers(Long gatheringId,List<Long> userIds) {
        Gathering gathering = gatheringRepository.findById(gatheringId).orElseThrow(() ->
                new BusinessException(ErrorCode.GATHERING_NOT_EXISTS));
        User writeUser = getUser();
        //권한을 가지고 있는지 검증
        gatheringUserRepository.findByAuthority(writeUser, gathering, Authority.WRITE)
                .orElseThrow(() -> new BusinessException(ErrorCode.NO_AUTHORITY));

        // 초대 할 대상자 조회
        List<User> usersToUpdate = userRepository.findAllById(userIds);
        validateUsers(usersToUpdate, userIds);

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
        removedUserIds.removeAll(userIds);  //업데이트 될 유저들 중 현재 존재하는 유저들
        removedUserIds.remove(writeUser.getId()); //현재 모임을 수정하는 유저

        removeUsersFromGathering(existingGatheringUsers, removedUserIds);
        addUsersToGathering(gathering, newUserIds);
    }

    @Transactional
    public void addUsersToGathering(Gathering gathering, Set<Long> newUserIds) {
        List<User> usersToInvite = userRepository.findAllById(newUserIds);
        User currentUser = getUser();

        Set<Long> foundUserIds = usersToInvite.stream()
                .map(User::getId)
                .collect(Collectors.toSet());

        if (!foundUserIds.containsAll(newUserIds)) {
            throw new BusinessException(ErrorCode.USER_INVALID);
        }

        if (newUserIds.contains(currentUser.getId())) {
            throw new BusinessException(ErrorCode.CANNOT_INVITE_SELF);
        }

        List<GatheringUser> newGatheringUsers = usersToInvite.stream()
                .map(user -> GatheringUser.builder()
                        .authority(Authority.READ)
                        .build()
                        .setUser(user)
                        .setGathering(gathering))
                .toList();;

        gatheringUserRepository.saveAll(newGatheringUsers);
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
     * 모든 유저를 찾아왔는지 검증하는 로직
     * @param usersToUpdate
     * @param userIds
     */
    private void validateUsers(List<User> usersToUpdate, List<Long> userIds) {
        if (usersToUpdate.size() != userIds.size()) {
            throw new BusinessException(ErrorCode.USER_NOT_EXISTS);
        }
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
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));
        GatheringUser gatheringUser = GatheringUser.builder()
                .authority(dto.getAuthority())
                .build();
        //연관관계 매핑
        gatheringUser.setUser(user);
        gatheringUser.setGathering(gathering);
        return gatheringUserRepository.save(gatheringUser);
    }

    /**
     * 모임 참석자 모두 조회하는 로직
     * @param gatheringId
     * @return
     */
    @Transactional(readOnly = true)
    public List<UserResponse> getGatheringUsers(Long gatheringId) {
        return gatheringUserRepository.findAllBy(gatheringId);
    }

    private User getUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        log.info("authentication : {}", authentication);
        Object principal = authentication.getPrincipal();
        if (principal instanceof PrincipalDetails) {

            SecurityUserDto securityUser = SecurityUserDto.from((PrincipalDetails) principal);
            Long userId = securityUser.getUserId();
            // 데이터베이스에서 사용자 조회
            return userRepository.findById(userId)
                    .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));
        } else {
            return null;
        }
    }
}
