package jungmo.server.global.auth.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jungmo.server.global.auth.dto.request.AuthorizationCodeRequest;
import jungmo.server.global.auth.dto.request.LoginRequestDto;
import jungmo.server.global.auth.dto.request.RefreshTokenRequestDto;
import jungmo.server.global.auth.dto.request.RegisterRequestDto;
import jungmo.server.global.auth.dto.response.TokenResponseDto;
import jungmo.server.global.auth.service.AuthService;
import jungmo.server.global.auth.service.PrincipalDetails;
import jungmo.server.global.auth.service.RedisService;
import jungmo.server.global.error.ErrorCode;
import jungmo.server.global.error.exception.AuthenticateException;
import jungmo.server.global.result.ResultCode;
import jungmo.server.global.result.ResultDetailResponse;
import jungmo.server.global.util.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResultDetailResponse<Void> register(@RequestBody @Valid RegisterRequestDto request) {
        authService.register(request);
        return new ResultDetailResponse(ResultCode.REGISTER_SUCCESS, null);
    }

    @PostMapping("/login")
    public ResultDetailResponse<TokenResponseDto> login(@RequestBody @Valid LoginRequestDto request, HttpServletResponse response) {
        TokenResponseDto tokenResponse = authService.login(request);
        // 액세스 토큰만 JSON 응답으로 반환
        return new ResultDetailResponse(ResultCode.LOGIN_SUCCESS,tokenResponse);
    }

    @GetMapping("/refresh")
    public ResultDetailResponse<TokenResponseDto> generateToken(@RequestBody @Valid RefreshTokenRequestDto request) {
        TokenResponseDto tokenResponse = authService.refreshToken(request);
        return new ResultDetailResponse<>(ResultCode.REFRESH_SUCCESS, tokenResponse);
    }

    @PostMapping("/kakao/token")
    public ResponseEntity<ResultDetailResponse<TokenResponseDto>> kakaoLogin(@RequestBody AuthorizationCodeRequest request) {
        TokenResponseDto tokenResponse = authService.authenticateWithKakao(request);
        return ResponseEntity.ok(new ResultDetailResponse<>(ResultCode.LOGIN_SUCCESS, tokenResponse));
    }

}

