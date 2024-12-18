package jungmo.server.domain.service;

import jungmo.server.domain.dto.request.GatheringDto;
import jungmo.server.domain.dto.request.GatheringUserDto;
import jungmo.server.domain.entity.Gathering;
import jungmo.server.domain.entity.GatheringUser;
import jungmo.server.domain.entity.User;
import jungmo.server.domain.repository.GatheringUserRepository;
import jungmo.server.domain.repository.UserRepository;
import jungmo.server.global.auth.dto.response.SecurityUserDto;
import jungmo.server.global.auth.service.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GatheringUserService {

    private final GatheringUserRepository gatheringUserRepository;
    private final UserRepository userRepository;

    @Transactional
    public GatheringUser saveGatheringUser(GatheringUserDto dto,Gathering gathering) {
        //모임유저 생성
        User user = getUser();
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
