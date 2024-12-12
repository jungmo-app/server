package jungmo.server.global.auth.controller;


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
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResultDetailResponse<Void> register(@RequestBody @Valid RegisterRequestDto request) {
        authService.register(request);
        return new ResultDetailResponse(ResultCode.REGISTER_SUCCESS, null);
    }

    @PostMapping("/login")
    public ResponseEntity<ResultDetailResponse<Void>> login(@RequestBody @Valid LoginRequestDto request, HttpServletResponse response) {
        authService.login(request,response);
        ResultDetailResponse<Void> result = new ResultDetailResponse<>(ResultCode.LOGIN_SUCCESS,null);
        return ResponseEntity.ok(result);

    }

    @PostMapping("/refresh")
    public ResponseEntity<ResultDetailResponse<Void>> generateToken(@RequestBody @Valid RefreshTokenRequestDto request, HttpServletResponse response) {
        authService.refreshToken(request,response);
        ResultDetailResponse<Void> result = new ResultDetailResponse<>(ResultCode.REFRESH_SUCCESS,null);
        return ResponseEntity.ok(result);
    }

}

