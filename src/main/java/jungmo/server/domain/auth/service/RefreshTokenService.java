package jungmo.server.domain.auth.service;

import jungmo.server.domain.auth.entity.RefreshToken;
import jungmo.server.domain.auth.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    // 토큰 저장
    public void saveTokens(String id, String accessToken, String refreshToken) {
        refreshTokenRepository.save(new RefreshToken(id, accessToken, refreshToken));
    }

    // 리프레시 토큰 검증
    public boolean validateRefreshToken(String id, String refreshToken) {
        return refreshTokenRepository.findById(id)
                .map(RefreshToken::getRefreshToken)
                .filter(token -> token.equals(refreshToken))
                .isPresent();
    }

    // 액세스 토큰 갱신
    public void updateAccessToken(String id, String newAccessToken) {
        refreshTokenRepository.findById(id).ifPresent(refreshToken -> {
            refreshToken.updateAccessToken(newAccessToken);
            refreshTokenRepository.save(refreshToken);
        });
    }

    // 리프레시 토큰 삭제
    public void deleteTokens(String id) {
        refreshTokenRepository.deleteById(id);
    }
}
