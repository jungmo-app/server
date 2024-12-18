package jungmo.server.domain.service;

import jungmo.server.domain.dto.request.GatheringUserDto;
import jungmo.server.domain.entity.GatheringUser;
import jungmo.server.domain.entity.User;
import jungmo.server.domain.repository.GatheringUserRepository;
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
public class GatheringUserService {

    private final GatheringUserRepository gatheringUserRepository;
    private final UserRepository userRepository;

    @Transactional
    public GatheringUser saveGatheringUser(GatheringUserDto dto) {
        User user = getUser();
        GatheringUser gatheringUser = GatheringUser.builder()
                .authority(dto.getAuthority())
                .status(dto.getStatus())
                .build();

        gatheringUser.setUser(user);
        GatheringUser savedUser = gatheringUserRepository.save(gatheringUser);
        return savedUser;
    }

    private User getUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        SecurityUserDto securityUser = (SecurityUserDto) authentication.getPrincipal();
        Long userId = securityUser.getUserId();
        return userRepository.findById(userId).orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));
    }
}
