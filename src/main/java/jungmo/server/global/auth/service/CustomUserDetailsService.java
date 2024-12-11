package jungmo.server.global.auth.service;

import org.springframework.security.core.userdetails.User;
import jungmo.server.domain.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email)
                .map(user -> User.builder()
                        .username(user.getEmail()) // Spring Security의 username 필드에 이메일 매핑
                        .password(user.getPassword()) // 비밀번호 설정
                        .roles(user.getRole()) // 역할 설정 (ROLE_ 접두사는 Spring Security가 자동 추가)
                        .build())
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
    }
}
