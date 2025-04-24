package jungmo.server.domain.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jungmo.server.domain.dto.request.UserCodeRequest;
import jungmo.server.domain.dto.request.UserRequest;
import jungmo.server.domain.dto.response.UserInfoResponse;
import jungmo.server.domain.dto.response.UserResponse;
import jungmo.server.global.result.ResultDetailResponse;
import jungmo.server.global.result.ResultListResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@Tag(name = "User", description = "사용자 관련 API")
public interface UserSwaggerController {

    @Operation(summary = "유저 조회 API", description = "고유코드로 유저를 조회하는 api.")
    public ResultListResponse<UserResponse> searchUser(@RequestParam String userCode);

    @Operation(summary = "로그인 한 유저 정보반환 API", description = "로그인 한 유저의 정보를 반환하는 api")
    public ResultDetailResponse<UserInfoResponse> getUser();

    @Operation(summary = "로그인 한 유저 정보 수정 API", description = "로그인 한 유저의 정보를 수정하는 api")
    public ResultDetailResponse<UserResponse> updateProfile(@ModelAttribute @Valid UserRequest userDto) throws IOException;

    @Operation(summary = "회원 탈퇴 API", description = "회원탈퇴를 하는 api")
    public ResponseEntity<?> deleteUser(HttpServletResponse response,
                                        @RequestHeader("Authorization") String accessToken,
                                        @CookieValue(value = "refreshToken", required = true) String refreshToken);
}
