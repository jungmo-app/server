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
public class UserController implements UserSwaggerController{

    private final UserService userService;

    @Override
    @GetMapping("/search")
    public ResultDetailResponse<UserResponse> searchUser(@RequestBody @Valid UserCodeRequest userCodeDto) {
        UserResponse user = userService.findUser(userCodeDto);
        return new ResultDetailResponse<>(ResultCode.GET_USER_SUCCESS, user);
    }

    @Override
    @GetMapping("/info")
    public ResultDetailResponse<UserResponse> getUser() {
        UserResponse userInfo = userService.getUserInfo();
        return new ResultDetailResponse<>(ResultCode.GET_MY_INFO_SUCCESS, userInfo);
    }

    @Override
    @PutMapping(value = "/info", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResultDetailResponse<UserResponse> updateProfile(@ModelAttribute @Valid UserRequest userDto) throws IOException {
        Long user_id = userService.updateUserProfile(userDto);
        UserResponse user = userService.findUserById(user_id);
        return new ResultDetailResponse<>(ResultCode.UPDATE_USER_INFO, user);
    }

    @Override
    @PutMapping("/password")
    public ResultDetailResponse<Void> changePassword(
            @RequestBody @Valid PasswordRequest request) {
        userService.changePassword(request);
        return new ResultDetailResponse<>(ResultCode.UPDATE_USER_PASSWORD, null);
    }
}
