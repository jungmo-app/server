package jungmo.server.global.auth.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jungmo.server.global.auth.dto.request.LoginRequestDto;
import jungmo.server.global.auth.dto.request.RefreshTokenRequestDto;
import jungmo.server.global.auth.dto.request.RegisterRequestDto;
import jungmo.server.global.result.ResultDetailResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@Tag(name = "register/login", description = "회원가입 및 로그인 관련 API")
public interface AuthSwaggerController {

    @Operation(summary = "회원가입" , description = "회원가입 API 액세스토큰 불필요")
    public ResponseEntity<ResultDetailResponse<Void>> register(@RequestBody @Valid RegisterRequestDto request, HttpServletResponse response);

    @Operation(summary = "로그인" , description = "로그인 API 액세스토큰 불필요")
    public ResponseEntity<ResultDetailResponse<Void>> login(@RequestBody @Valid LoginRequestDto request, HttpServletResponse response);

    @Operation(summary = "토큰 리프레시" , description = "액세스, 리프레시 토큰 재발급 API")
    public ResponseEntity<ResultDetailResponse<Void>> generateToken(@RequestBody @Valid RefreshTokenRequestDto request, HttpServletResponse response);

    @Operation(summary = "로그아웃" , description = "로그아웃 API")
    public ResponseEntity<?> logout(
            @RequestHeader("Authorization") String accessToken,
            @CookieValue(value = "refreshToken", required = true) String refreshToken);
}
