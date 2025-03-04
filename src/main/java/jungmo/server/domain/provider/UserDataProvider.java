package jungmo.server.domain.provider;

import jungmo.server.domain.entity.User;
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
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserDataProvider {

    private final UserRepository userRepository;

    public User getUser() {
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

    public User findUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_EXISTS));
        if (user.getIsDeleted() == false) {
            throw new BusinessException(ErrorCode.USER_NOT_EXISTS);
        }
        return user;
    }

    public List<User> findUserByUserCode(String userCode) {
        return userRepository.findByUserCodeStartingWith(userCode);
    }
}
