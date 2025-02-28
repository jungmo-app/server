package jungmo.server.global.auth.service;

import jungmo.server.domain.entity.User;
import jungmo.server.domain.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        log.debug("🔥 CustomUserDetailsService - loadUserByUsername() 실행: {}", email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        PrincipalDetails principalDetails = new PrincipalDetails(user);
        log.debug("✅ PrincipalDetails 생성 완료: {}", principalDetails);

        return principalDetails;
    }

}
