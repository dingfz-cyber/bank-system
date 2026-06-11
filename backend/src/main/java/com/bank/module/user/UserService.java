package com.bank.module.user;

import com.bank.module.user.dto.LoginDto;
import com.bank.module.user.dto.RegisterDto;
import com.bank.module.user.dto.UserVo;

/**
 * 用户服务接口
 */
public interface UserService {

    /**
     * 用户注册
     */
    void register(RegisterDto dto);

    /**
     * 用户登录
     */
    UserVo login(LoginDto dto);

    /**
     * 获取用户信息
     */
    UserVo getUserInfo(Long userId);

    /**
     * 修改个人信息
     */
    void updateProfile(Long userId, String nickName);

    /**
     * 修改密码
     */
    void changePassword(Long userId, String oldPassword, String newPassword);

    /**
     * 设置/修改交易密码
     */
    void setTransactionPassword(Long userId, String password);

    /**
     * 短信验证码登录
     */
    UserVo smsLogin(String phone, String code);

    /**
     * 忘记密码—重置登录密码
     */
    void resetPassword(String phone, String newPassword);

    /**
     * 全量用户列表
     */
    java.util.List<User> listAll();

    /**
     * 锁定用户
     */
    void lockUser(Long id);

    /**
     * 解锁用户
     */
    void unlockUser(Long id);

    /**
     * 管理员重置用户密码
     */
    void adminResetPassword(Long id);
}
