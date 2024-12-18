package jungmo.server.global.auth.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jungmo.server.global.auth.dto.request.LoginRequestDto;
import jungmo.server.global.auth.dto.request.RefreshTokenRequestDto;
import jungmo.server.global.auth.dto.request.RegisterRequestDto;
import jungmo.server.global.auth.dto.response.TokenResponseDto;
import jungmo.server.global.auth.service.AuthService;
import jungmo.server.global.result.ResultCode;
import jungmo.server.global.result.ResultDetailResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "register/login", description = "회원가입 및 로그인 관련 API")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    @Operation(summary = "회원가입" , description = "회원가입 API")
    public ResponseEntity<ResultDetailResponse<Void>> register(@RequestBody @Valid RegisterRequestDto request, HttpServletResponse response) {
        authService.register(request,response);
        ResultDetailResponse<Void> result = new ResultDetailResponse<>(ResultCode.REGISTER_SUCCESS, null);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/login")
    @Operation(summary = "로그인" , description = "로그인 API")
    public ResponseEntity<ResultDetailResponse<Void>> login(@RequestBody @Valid LoginRequestDto request, HttpServletResponse response) {
        authService.login(request,response);
        ResultDetailResponse<Void> result = new ResultDetailResponse<>(ResultCode.LOGIN_SUCCESS,null);
        return ResponseEntity.ok(result);

    }

    @PostMapping("/refresh")
    @Operation(summary = "토큰 리프레시" , description = "액세스, 리프레시 토큰 재발급 API")
    public ResponseEntity<ResultDetailResponse<Void>> generateToken(@RequestBody @Valid RefreshTokenRequestDto request, HttpServletResponse response) {
        authService.refreshToken(request,response);
        ResultDetailResponse<Void> result = new ResultDetailResponse<>(ResultCode.REFRESH_SUCCESS,null);
        return ResponseEntity.ok(result);
    }

}

