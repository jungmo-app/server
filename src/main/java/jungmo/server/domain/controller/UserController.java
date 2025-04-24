package jungmo.server.domain.controller;


import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jungmo.server.domain.dto.request.UserCodeRequest;
import jungmo.server.domain.dto.request.UserRequest;
import jungmo.server.domain.dto.response.UserInfoResponse;
import jungmo.server.domain.dto.response.UserResponse;
import jungmo.server.domain.service.UserService;
import jungmo.server.global.result.ResultCode;
import jungmo.server.global.result.ResultDetailResponse;
import jungmo.server.global.result.ResultListResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController implements UserSwaggerController {

    private final UserService userService;

    @Override
    @GetMapping("/search")
    public ResultListResponse<UserResponse> searchUser(@RequestParam String userCode) {
        return new ResultListResponse<>(ResultCode.GET_USER_SUCCESS, userService.findUser(userCode));
    }

    @Override
    @GetMapping("/info")
    public ResultDetailResponse<UserInfoResponse> getUser() {
        return new ResultDetailResponse<>(ResultCode.GET_MY_INFO_SUCCESS, userService.getUserInfo());
    }

    @Override
    @PutMapping(value = "/info", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResultDetailResponse<UserResponse> updateProfile(@ModelAttribute @Valid UserRequest userDto) throws IOException {
        return new ResultDetailResponse<>(ResultCode.UPDATE_USER_INFO,
                userService.findUserById(userService.updateUserProfile(userDto)));
    }

    @DeleteMapping("/info")
    public ResponseEntity<?> deleteUser(HttpServletResponse response,
                                                 @RequestHeader("Authorization") String accessToken,
                                                 @CookieValue(value = "refreshToken", required = true) String refreshToken) {

        accessToken = accessToken.replace("Bearer ", "");
        userService.deleteUser(response,accessToken, refreshToken);
        ResultDetailResponse<Void> result = new ResultDetailResponse<>(ResultCode.DELETE_USER, null);
        return ResponseEntity.ok(result);
    }
}
