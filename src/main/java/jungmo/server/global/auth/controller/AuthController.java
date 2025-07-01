package jungmo.server.global.auth.controller;



import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jungmo.server.domain.dto.request.PasswordRequest;
import jungmo.server.domain.dto.request.PasswordResetRequest;
import jungmo.server.domain.dto.request.ResetPasswordRequest;
import jungmo.server.domain.service.UserService;
import jungmo.server.global.auth.dto.request.KakaoTokenRequest;
import jungmo.server.global.auth.dto.request.LoginRequestDto;
import jungmo.server.global.auth.dto.request.RegisterRequestDto;
import jungmo.server.global.auth.dto.response.AccessTokenResponse;
import jungmo.server.global.auth.dto.response.TokenResponseDto;
import jungmo.server.global.auth.dto.response.UserLoginResponse;
import jungmo.server.global.auth.service.AuthService;
import jungmo.server.global.result.ResultCode;
import jungmo.server.global.result.ResultDetailResponse;
import jungmo.server.global.util.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor

public class AuthController implements AuthSwaggerController{

    private final AuthService authService;
    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    @GetMapping("/isBlacklist")
    public ResultDetailResponse<Boolean> isBlackList(@RequestParam String accessToken) {
        return new ResultDetailResponse<>(ResultCode.IS_TOKEN_BLACKLIST, jwtTokenProvider.isTokenBlacklisted(accessToken));
    }

    @PostMapping("/test/login")
    public ResultDetailResponse<TokenResponseDto> testLogin(String email) {
        return new ResultDetailResponse<>(ResultCode.LOGIN_SUCCESS,authService.testLogin(email));

    }

    @Override
    @PostMapping("/register")
    public ResponseEntity<ResultDetailResponse<UserLoginResponse>> register(@RequestBody @Valid RegisterRequestDto request, HttpServletResponse response) {
        return ResponseEntity.ok(new ResultDetailResponse<>(ResultCode.REGISTER_SUCCESS, authService.register(request, response)));
    }

    @Override
    @PostMapping("/login")
    public ResponseEntity<ResultDetailResponse<UserLoginResponse>> login(@RequestBody @Valid LoginRequestDto request, HttpServletResponse response) {
        return ResponseEntity.ok(new ResultDetailResponse<>(ResultCode.LOGIN_SUCCESS,authService.login(request, response)));

    }

    @PostMapping("/kakao")
    public ResponseEntity<ResultDetailResponse<UserLoginResponse>> kakaoLogin(@RequestBody KakaoTokenRequest request, HttpServletResponse response) {
        return ResponseEntity.ok(new ResultDetailResponse<>(ResultCode.LOGIN_SUCCESS,authService.kakaoAppLogin(request,response)));
    }

    @Override
    @PutMapping("/password")
    public ResultDetailResponse<Void> changePassword(
            @RequestBody @Valid PasswordRequest request) {
        userService.changePassword(request);
        return new ResultDetailResponse<>(ResultCode.UPDATE_USER_PASSWORD, null);
    }

    @Override
    @PostMapping("/set-password")
    public ResultDetailResponse<Void> sendResetPasswordEmail(@RequestBody @Valid PasswordResetRequest request) {
        authService.processPasswordResetRequest(request);
        return new ResultDetailResponse<>(ResultCode.SEND_EMAIL, null);
    }

    @Override
    @PatchMapping("/reset-password")
    public ResultDetailResponse<Void> resetPassword(@RequestBody @Valid ResetPasswordRequest request) {
        authService.resetPassword(request.getToken(), request.getNewPassword());
        return new ResultDetailResponse<>(ResultCode.UPDATE_USER_PASSWORD, null);
    }

    @Override
    @PostMapping("/refresh")
    public ResponseEntity<ResultDetailResponse<AccessTokenResponse>> generateToken(
            @CookieValue("refreshToken") String refreshToken,
            HttpServletResponse response) {
        return ResponseEntity.ok(new ResultDetailResponse<>(ResultCode.REFRESH_SUCCESS,authService.refreshToken(refreshToken, response)));
    }

    @Override
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse response,
            @RequestHeader(value = "Authorization", required = true) String accessToken,
            @CookieValue(value = "refreshToken", required = true) String refreshToken) {
        accessToken = accessToken.replace("Bearer ", "");
        userService.logout(response,accessToken, refreshToken);
        ResultDetailResponse<Void> result = new ResultDetailResponse<>(ResultCode.LOGOUT_SUCCESS, null);
        return ResponseEntity.ok(result);
    }

}

