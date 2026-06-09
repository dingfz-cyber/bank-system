package com.bank.module.user;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.bank.common.BusinessException;
import com.bank.config.JwtUtil;
import com.bank.module.user.dto.LoginDto;
import com.bank.module.user.dto.RegisterDto;
import com.bank.module.user.dto.UserVo;
import com.bank.log.OperationLog;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * 用户服务实现
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final JwtUtil jwtUtil;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    @OperationLog(value = "用户注册", type = "register")
    public void register(RegisterDto dto) {
        // 检查手机号是否已注册
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getPhone, dto.getPhone());
        if (userMapper.selectCount(queryWrapper) > 0) {
            throw new BusinessException("该手机号已注册");
        }

        // 创建用户
        User user = new User();
        user.setPhone(dto.getPhone());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setNickName(dto.getNickName());
        user.setRoleId(3L); // 默认普通用户
        userMapper.insert(user);
    }

    @Override
    @OperationLog(value = "用户登录", type = "login")
    public UserVo login(LoginDto dto) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getPhone, dto.getPhone());
        User user = userMapper.selectOne(queryWrapper);
        if (user == null) {
            throw new BusinessException("账号或密码错误");
        }

        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new BusinessException("账号或密码错误");
        }

        String token = jwtUtil.generateToken(user.getId(), user.getPhone(), user.getRoleId());

        UserVo vo = new UserVo();
        vo.setId(user.getId());
        vo.setPhone(user.getPhone());
        vo.setNickName(user.getNickName());
        vo.setRoleId(user.getRoleId());
        vo.setToken(token);
        return vo;
    }

    @Override
    public UserVo getUserInfo(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        UserVo vo = new UserVo();
        vo.setId(user.getId());
        vo.setPhone(user.getPhone());
        vo.setNickName(user.getNickName());
        vo.setRoleId(user.getRoleId());
        return vo;
    }
}
