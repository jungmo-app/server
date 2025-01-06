package jungmo.server.global.auth.controller;



import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jungmo.server.global.auth.dto.request.LoginRequestDto;
import jungmo.server.global.auth.dto.request.RefreshTokenRequestDto;
import jungmo.server.global.auth.dto.request.RegisterRequestDto;
import jungmo.server.global.auth.service.AuthService;
import jungmo.server.global.result.ResultCode;
import jungmo.server.global.result.ResultDetailResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor

public class AuthController implements AuthSwaggerController{

    private final AuthService authService;

    @Override
    @PostMapping("/register")
    public ResponseEntity<ResultDetailResponse<Void>> register(@RequestBody @Valid RegisterRequestDto request, HttpServletResponse response) {
        authService.register(request,response);
        ResultDetailResponse<Void> result = new ResultDetailResponse<>(ResultCode.REGISTER_SUCCESS, null);
        return ResponseEntity.ok(result);
    }

    @Override
    @PostMapping("/login")
    public ResponseEntity<ResultDetailResponse<Void>> login(@RequestBody @Valid LoginRequestDto request, HttpServletResponse response) {
        authService.login(request,response);
        ResultDetailResponse<Void> result = new ResultDetailResponse<>(ResultCode.LOGIN_SUCCESS,null);
        return ResponseEntity.ok(result);

    }

    @Override
    @PostMapping("/refresh")
    public ResponseEntity<ResultDetailResponse<Void>> generateToken(@RequestBody @Valid RefreshTokenRequestDto request, HttpServletResponse response) {
        authService.refreshToken(request,response);
        ResultDetailResponse<Void> result = new ResultDetailResponse<>(ResultCode.REFRESH_SUCCESS,null);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(
            @RequestHeader("Authorization") String accessToken,
            @CookieValue(value = "refreshToken", required = false) String refreshToken) {
        accessToken = accessToken.replace("Bearer ", ""); // Bearer 제거
        authService.logout(accessToken, refreshToken);
        return ResponseEntity.ok(new ResultDetailResponse<>(ResultCode.LOGOUT_SUCCESS, null));
    }

}

