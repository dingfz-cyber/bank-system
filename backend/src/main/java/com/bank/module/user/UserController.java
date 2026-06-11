package com.bank.module.user;

import com.bank.common.Result;
import com.bank.module.user.dto.LoginDto;
import com.bank.module.user.dto.PasswordDto;
import com.bank.module.user.dto.ProfileDto;
import com.bank.module.user.dto.RegisterDto;
import com.bank.module.user.dto.UserVo;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import com.bank.rbac.RequireRole;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
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

    /**
     * 修改个人信息（需登录）
     */
    @PutMapping("/profile")
    public Result<Void> updateProfile(@Valid @RequestBody ProfileDto dto, HttpServletRequest request) {
        Long userId = Long.parseLong(request.getAttribute("userId").toString());
        userService.updateProfile(userId, dto.getNickName());
        return Result.success();
    }

    /**
     * 修改密码（需登录）
     */
    @PutMapping("/password")
    public Result<Void> changePassword(@Valid @RequestBody PasswordDto dto, HttpServletRequest request) {
        Long userId = Long.parseLong(request.getAttribute("userId").toString());
        userService.changePassword(userId, dto.getOldPassword(), dto.getNewPassword());
        return Result.success();
    }

    /**
     * 设置交易密码（需登录）
     */
    @PutMapping("/transaction-password")
    public Result<Void> setTransactionPassword(@RequestBody PasswordDto dto, HttpServletRequest request) {
        Long userId = Long.parseLong(request.getAttribute("userId").toString());
        userService.setTransactionPassword(userId, dto.getNewPassword());
        return Result.success();
    }

    /**
     * 短信验证码登录
     */
    @PostMapping("/sms-login")
    public Result<UserVo> smsLogin(@RequestBody LoginDto dto) {
        return Result.success(userService.smsLogin(dto.getPhone(), dto.getPassword()));
    }

    /**
     * 忘记密码—通过手机号重置登录密码
     */
    @PostMapping("/forgot-password")
    public Result<Void> forgotPassword(@RequestBody RegisterDto dto) {
        userService.resetPassword(dto.getPhone(), dto.getPassword());
        return Result.success();
    }

    /**
     * 全量用户列表（管理员）
     */
    @GetMapping("/list")
    @RequireRole({"SYS_ADMIN", "OPERATOR"})
    public Result<java.util.List<User>> listUsers() {
        return Result.success(userService.listAll());
    }

    /**
     * 锁定用户（管理员）
     */
    @PostMapping("/lock/{id}")
    @RequireRole({"SYS_ADMIN", "OPERATOR"})
    public Result<Void> lockUser(@PathVariable Long id) {
        userService.lockUser(id);
        return Result.success();
    }

    /**
     * 解锁用户（管理员）
     */
    @PostMapping("/unlock/{id}")
    @RequireRole({"SYS_ADMIN", "OPERATOR"})
    public Result<Void> unlockUser(@PathVariable Long id) {
        userService.unlockUser(id);
        return Result.success();
    }

    /**
     * 管理员重置用户密码（重置为123456）
     */
    @PostMapping("/admin/reset-password/{id}")
    @RequireRole({"SYS_ADMIN", "OPERATOR"})
    public Result<Void> adminResetPassword(@PathVariable Long id) {
        userService.adminResetPassword(id);
        return Result.success();
    }

    private final LoginRecordMapper loginRecordMapper;

    /**
     * 我的登录记录
     */
    @GetMapping("/login-records")
    public Result<java.util.List<LoginRecord>> loginRecords(HttpServletRequest request) {
        Long userId = Long.parseLong(request.getAttribute("userId").toString());
        return Result.success(loginRecordMapper.selectList(
            new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<LoginRecord>()
                .eq(LoginRecord::getUserId, userId).orderByDesc(LoginRecord::getLoginTime).last("LIMIT 20")));
    }
}
