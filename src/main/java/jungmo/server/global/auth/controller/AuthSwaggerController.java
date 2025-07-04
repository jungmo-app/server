package jungmo.server.global.auth.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jungmo.server.domain.dto.request.PasswordRequest;
import jungmo.server.domain.dto.request.PasswordResetRequest;
import jungmo.server.domain.dto.request.ResetPasswordRequest;
import jungmo.server.global.auth.dto.request.KakaoTokenRequest;
import jungmo.server.global.auth.dto.request.LoginRequestDto;
import jungmo.server.global.auth.dto.request.RegisterRequestDto;
import jungmo.server.global.auth.dto.response.AccessTokenResponse;
import jungmo.server.global.auth.dto.response.UserLoginResponse;
import jungmo.server.global.result.ResultDetailResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "register/login", description = "회원가입 및 로그인 관련 API")
public interface AuthSwaggerController {

    @Operation(summary = "블랙리스트 있는지 확인하는 API" , description = "미들웨어에서 사용")
    public ResultDetailResponse<Boolean> isBlackList(@RequestParam String accessToken);

    @Operation(summary = "회원가입" , description = "회원가입 API 액세스토큰 불필요")
    public ResponseEntity<ResultDetailResponse<UserLoginResponse>> register(@RequestBody @Valid RegisterRequestDto request, HttpServletResponse response);

    @Operation(summary = "로그인" , description = "로그인 API 액세스토큰 불필요")
    public ResponseEntity<ResultDetailResponse<UserLoginResponse>> login(@RequestBody @Valid LoginRequestDto request, HttpServletResponse response);

    @Operation(summary = "네이티브앱용 카카오로그인")
    public ResponseEntity<ResultDetailResponse<UserLoginResponse>> kakaoLogin(@RequestBody KakaoTokenRequest request, HttpServletResponse response);

    @Operation(summary = "로그인 한 유저 비밀번호 수정 API", description = "로그인 한 유저의 비밀번호를 수정하는 api")
    public ResultDetailResponse<Void> changePassword(
            @RequestBody @Valid PasswordRequest request);

    @Operation(summary = "비밀번호 재설정 링크 이메일로 보내는 API", description = "비밀번호 재설정하는 링크를 사용자의 이메일로 보내주는 api")
    public ResultDetailResponse<Void> sendResetPasswordEmail(@RequestBody @Valid PasswordResetRequest request);

    @Operation(summary = "비밀번호 재설정하는 API, 비밀번호 잊어버린 사용자용", description = "비밀번호 재설정하는 api")
    public ResultDetailResponse<Void> resetPassword(@RequestBody @Valid ResetPasswordRequest request);

    @Operation(summary = "토큰 리프레시" , description = "액세스, 리프레시 토큰 재발급 API")
    public ResponseEntity<ResultDetailResponse<AccessTokenResponse>> generateToken(
            @CookieValue("refresh-token") String refreshToken,
            HttpServletResponse response);

    @Operation(summary = "로그아웃" , description = "로그아웃 API")
    public ResponseEntity<?> logout(HttpServletResponse response,
                                    @RequestHeader(value = "Authorization", required = true) String accessToken,
                                    @CookieValue(value = "refreshToken", required = true) String refreshToken);
}
