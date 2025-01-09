package jungmo.server.domain.controller;


import jakarta.validation.Valid;
import jungmo.server.domain.dto.request.PasswordRequest;
import jungmo.server.domain.dto.request.UserCodeRequest;
import jungmo.server.domain.dto.request.UserRequest;
import jungmo.server.domain.dto.response.UserResponse;
import jungmo.server.domain.service.UserService;
import jungmo.server.global.result.ResultCode;
import jungmo.server.global.result.ResultDetailResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController implements UserSwaggerController {

    private final UserService userService;

    @Override
    @GetMapping("/search")
    public ResultDetailResponse<UserResponse> searchUser(@RequestBody @Valid UserCodeRequest userCodeDto) {
        return new ResultDetailResponse<>(ResultCode.GET_USER_SUCCESS, userService.findUser(userCodeDto));
    }

    @Override
    @GetMapping("/info")
    public ResultDetailResponse<UserResponse> getUser() {
        return new ResultDetailResponse<>(ResultCode.GET_MY_INFO_SUCCESS, userService.getUserInfo());
    }

    @Override
    @PutMapping(value = "/info", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResultDetailResponse<UserResponse> updateProfile(@ModelAttribute @Valid UserRequest userDto) throws IOException {
        return new ResultDetailResponse<>(ResultCode.UPDATE_USER_INFO,
                userService.findUserById(userService.updateUserProfile(userDto)));
    }

    @Override
    @PutMapping("/password")
    public ResultDetailResponse<Void> changePassword(
            @RequestBody @Valid PasswordRequest request) {
        userService.changePassword(request);
        return new ResultDetailResponse<>(ResultCode.UPDATE_USER_PASSWORD, null);
    }

    @DeleteMapping("/info")
    public ResultDetailResponse<Void> deleteUser(
            @RequestHeader("Authorization") String accessToken,
            @CookieValue(value = "refreshToken", required = true) String refreshToken) {

        accessToken = accessToken.replace("Bearer ", "");
        userService.deleteUser(accessToken, refreshToken);
        return new ResultDetailResponse<>(ResultCode.DELETE_USER, null);
    }
}
