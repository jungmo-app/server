package jungmo.server.domain.auth.service;

import jungmo.server.domain.auth.dto.LoginRequest;
import jungmo.server.domain.auth.dto.RegisterRequest;
import jungmo.server.domain.auth.dto.TokenResponse;
import jungmo.server.domain.auth.repository.UserRepository;
import jungmo.server.global.util.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import jungmo.server.domain.entity.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenService refreshTokenService;

    public void register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already in use");
        }

        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .userName(request.getName())
                .provider("email")
                .role("ROLE_USER")
                .build();

        userRepository.save(user);
    }

    public TokenResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Invalid email or password");
        }

        String accessToken = jwtTokenProvider.generateAccessToken(user.getEmail());
        String refreshToken = jwtTokenProvider.generateRefreshToken(user.getEmail());

        refreshTokenService.saveTokens(user.getEmail(), accessToken, refreshToken);

        return new TokenResponse(accessToken, refreshToken);
    }
}
