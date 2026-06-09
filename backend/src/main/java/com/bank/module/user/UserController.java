package com.bank.module.user;

import com.bank.common.Result;
import com.bank.module.user.dto.LoginDto;
import com.bank.module.user.dto.RegisterDto;
import com.bank.module.user.dto.UserVo;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 用户控制器
 */
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * 用户注册
     */
    @PostMapping("/register")
    public Result<Void> register(@Valid @RequestBody RegisterDto dto) {
        userService.register(dto);
        return Result.success();
    }

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public Result<UserVo> login(@Valid @RequestBody LoginDto dto) {
        return Result.success(userService.login(dto));
    }

    /**
     * 退出登录（前端清除 token 即可，服务端无状态）
     */
    @PostMapping("/logout")
    public Result<Void> logout() {
        return Result.success();
    }

    /**
     * 获取当前用户信息
     */
    @GetMapping("/info")
    public Result<UserVo> info(HttpServletRequest request) {
        Long userId = Long.parseLong(request.getAttribute("userId").toString());
        return Result.success(userService.getUserInfo(userId));
    }
}
