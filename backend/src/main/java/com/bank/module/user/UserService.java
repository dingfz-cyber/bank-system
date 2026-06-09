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
}
