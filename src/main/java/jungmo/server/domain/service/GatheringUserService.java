package jungmo.server.domain.service;

import jungmo.server.domain.dto.request.GatheringUserDto;
import jungmo.server.domain.dto.response.UserDto;
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

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GatheringUserService {

    private final GatheringUserRepository gatheringUserRepository;
    private final UserRepository userRepository;
    private final GatheringRepository gatheringRepository;

    @Transactional
    public void inviteUser(Long gatheringId,Long userId) {
        Gathering gathering = gatheringRepository.findById(gatheringId).orElseThrow(() ->
                new BusinessException(ErrorCode.GATHERING_NOT_EXISTS));
        User writeUser = getUser();
        Optional<GatheringUser> optionalGatheringUser = gatheringUserRepository.findByAuthority(writeUser, gathering, Authority.WRITE);
        if (optionalGatheringUser.isPresent()) {
            GatheringUserDto gatheringUserDto = new GatheringUserDto(Authority.READ, GatheringStatus.PENDING);
            saveGatheringUser(userId,gatheringUserDto,gathering);
        } else throw new BusinessException(ErrorCode.NO_AUTHORITY);
    }

    @Transactional
    public GatheringUser saveGatheringUser(Long userId, GatheringUserDto dto,Gathering gathering) {
        //모임유저 생성
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));
        GatheringUser gatheringUser = GatheringUser.builder()
                .authority(dto.getAuthority())
                .status(dto.getStatus())
                .build();
        //연관관계 매핑
        gatheringUser.setUser(user);
        gatheringUser.setGathering(gathering);
        GatheringUser savedUser = gatheringUserRepository.save(gatheringUser);
        return savedUser;
    }

    @Transactional
    public void acceptInvitation(Long gatheringId) {
        User user = getUser();
        Optional<GatheringUser> pendingUser = gatheringUserRepository.findGatheringUserByUserIdAndGatheringId(user.getId(), gatheringId);
        if (pendingUser.isPresent()) {
            if (pendingUser.get().getStatus() == GatheringStatus.PENDING) {
                pendingUser.get().setStatus(GatheringStatus.ACCEPT);
            } else throw new BusinessException(ErrorCode.ALREADY_CHOOSE);
        } else throw new BusinessException(ErrorCode.INVITATION_NOT_EXISTS);
    }

    @Transactional
    public void rejectInvitation(Long gatheringId) {
        User user = getUser();
        Optional<GatheringUser> pendingUser = gatheringUserRepository.findGatheringUserByUserIdAndGatheringId(user.getId(), gatheringId);
        if (pendingUser.isPresent()) {
            if (pendingUser.get().getStatus() == GatheringStatus.PENDING) {
                pendingUser.get().setStatus(GatheringStatus.REJECT);
            } else throw new BusinessException(ErrorCode.ALREADY_CHOOSE);
        } else throw new BusinessException(ErrorCode.INVITATION_NOT_EXISTS);
    }

    public List<UserDto> getGatheringUsers(Long gatheringId) {
        User user = getUser();
        Optional<GatheringUser> gatheringUser = gatheringUserRepository.findGatheringUserByUserIdAndGatheringId(user.getId(), gatheringId);
        if (gatheringUser.isPresent()) {
            return gatheringUserRepository.findAllBy(gatheringId, GatheringStatus.ACCEPT);
        } else throw new BusinessException(ErrorCode.NOT_A_GATHERING_USER);
    }

    @Transactional
    public void export(Long gatheringId, Long gatheringUserId) {
        User user = getUser();
        Gathering gathering = gatheringRepository.findById(gatheringId).orElseThrow(() ->
                new BusinessException(ErrorCode.GATHERING_NOT_EXISTS));
        Optional<GatheringUser> gatheringUser = gatheringUserRepository.findByAuthority(user, gathering, Authority.WRITE);

        if (gatheringUser.isPresent()) {
            GatheringUser exportedUser = gatheringUserRepository.findById(gatheringUserId).orElseThrow(
                    () -> new BusinessException(ErrorCode.GATHERING_USER_NOT_EXISTS)
            );
            if (exportedUser.getGathering() == gathering && exportedUser.getStatus() == GatheringStatus.ACCEPT) {
                exportedUser.removeGathering(gathering);
            }
            else throw new BusinessException(ErrorCode.NOT_A_GATHERING_USER);
        } else throw new BusinessException(ErrorCode.NO_AUTHORITY);
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
