package jungmo.server.domain.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jungmo.server.domain.dto.request.UserCodeDto;
import jungmo.server.domain.dto.request.UserRequestDto;
import jungmo.server.domain.dto.response.UserDto;
import jungmo.server.domain.entity.User;
import jungmo.server.domain.service.UserService;
import jungmo.server.global.result.ResultCode;
import jungmo.server.global.result.ResultDetailResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
@Tag(name = "User", description = "사용자 관련 API")
public class UserController {

    private final UserService userService;

    @GetMapping("/search")
    @Operation(summary = "유저 조회 API", description = "고유코드로 유저를 조회하는 api.")
    public ResultDetailResponse<UserDto> searchUser(@RequestBody @Valid UserCodeDto userCodeDto) {
        UserDto user = userService.findUser(userCodeDto);
        return new ResultDetailResponse<>(ResultCode.GET_USER_SUCCESS, user);
    }

    @GetMapping("/info")
    @Operation(summary = "로그인 한 유저 정보반환 API", description = "로그인 한 유저의 정보를 반환하는 api")
    public ResultDetailResponse<UserDto> getUser() {
        UserDto userInfo = userService.getUserInfo();
        return new ResultDetailResponse<>(ResultCode.GET_MY_INFO_SUCCESS, userInfo);
    }
    @PutMapping(value = "/info", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "로그인 한 유저 정보 수정 API", description = "로그인 한 유저의 정보를 수정하는 api")
    public ResultDetailResponse<UserDto> updateProfile(@ModelAttribute @Valid UserRequestDto userDto) throws IOException {
        Long user_id = userService.updateUserProfile(userDto);
        UserDto user = userService.findUserById(user_id);
        return new ResultDetailResponse<>(ResultCode.UPDATE_USER_INFO, user);
    }

}
