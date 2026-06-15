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

import java.time.LocalDateTime;

/**
 * 用户服务实现
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final JwtUtil jwtUtil;
    private final LoginRecordMapper loginRecordMapper;
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
        user.setRealName(dto.getRealName());
        user.setIdCard(dto.getIdCard());
        user.setRoleId(5L); // 默认普通用户
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

        // 检查账号是否被锁定
        if (user.getLockedAt() != null && user.getLockedAt().isAfter(LocalDateTime.now())) {
            throw new BusinessException("账号已被锁定，请30分钟后再试");
        }

        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            // 登录失败：累加失败次数，达到5次锁定30分钟
            int attempts = user.getLoginAttempts() == null ? 0 : user.getLoginAttempts();
            attempts++;
            user.setLoginAttempts(attempts);
            if (attempts >= 5) {
                user.setLockedAt(LocalDateTime.now().plusMinutes(30));
                userMapper.updateById(user);
                throw new BusinessException("密码连续错误5次，账号已锁定30分钟");
            }
            userMapper.updateById(user);
            throw new BusinessException("账号或密码错误，剩余尝试次数：" + (5 - attempts));
        }

        // 登录成功：重置失败计数和锁定状态
        user.setLoginAttempts(0);
        user.setLockedAt(null);
        userMapper.updateById(user);

        // 普通用户每日登陆积分 +5
        if (user.getRoleId() == 5) {
            user.setPoints(user.getPoints()==null?5:user.getPoints()+5);
        }

        // 记录登录
        LoginRecord lr = new LoginRecord();
        lr.setUserId(user.getId()); lr.setPhone(user.getPhone());
        lr.setLoginTime(LocalDateTime.now()); lr.setIp("127.0.0.1"); lr.setDevice("Web");
        loginRecordMapper.insert(lr);

        String token = jwtUtil.generateToken(user.getId(), user.getPhone(), user.getRoleId());
        UserVo vo = new UserVo();
        vo.setId(user.getId()); vo.setPhone(user.getPhone());
        vo.setNickName(user.getNickName()); vo.setRoleId(user.getRoleId()); vo.setPoints(user.getPoints());
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

    @Override
    @OperationLog(value = "修改个人信息", type = "update")
    public void updateProfile(Long userId, String nickName) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        user.setNickName(nickName);
        userMapper.updateById(user);
    }

    @Override
    @OperationLog(value = "修改密码", type = "update")
    public void changePassword(Long userId, String oldPassword, String newPassword) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new BusinessException("原密码错误");
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        userMapper.updateById(user);
    }

    @Override
    @OperationLog(value = "设置交易密码", type = "update")
    public void setTransactionPassword(Long userId, String password) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        if (password == null || !password.matches("\\d{6}")) {
            throw new BusinessException("交易密码必须为6位数字");
        }
        user.setTransactionPassword(passwordEncoder.encode(password));
        userMapper.updateById(user);
    }

    @Override
    @OperationLog(value = "短信验证码登录", type = "login")
    public UserVo smsLogin(String phone, String code) {
        // 演示用固定验证码 888888
        if (code == null || !code.equals("888888")) {
            throw new BusinessException("验证码错误");
        }
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getPhone, phone);
        User user = userMapper.selectOne(wrapper);
        if (user == null) {
            throw new BusinessException("该手机号未注册");
        }
        if (user.getLockedAt() != null && user.getLockedAt().isAfter(LocalDateTime.now())) {
            throw new BusinessException("账号已被锁定");
        }
        String token = jwtUtil.generateToken(user.getId(), user.getPhone(), user.getRoleId());
        UserVo vo = new UserVo();
        vo.setId(user.getId()); vo.setPhone(user.getPhone());
        vo.setNickName(user.getNickName()); vo.setRoleId(user.getRoleId()); vo.setPoints(user.getPoints());
        vo.setToken(token);
        return vo;
    }

    @Override
    @OperationLog(value = "重置登录密码", type = "update")
    public void resetPassword(String phone, String newPassword) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getPhone, phone);
        User user = userMapper.selectOne(queryWrapper);
        if (user == null) {
            throw new BusinessException("该手机号未注册");
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setLoginAttempts(0);
        user.setLockedAt(null);
        userMapper.updateById(user);
    }

    @Override
    public java.util.List<User> listAll() {
        return userMapper.selectList(null);
    }

    @Override
    @OperationLog(value = "锁定用户", type = "update")
    public void lockUser(Long id) {
        User user = userMapper.selectById(id);
        if (user == null) throw new BusinessException("用户不存在");
        user.setLockedAt(LocalDateTime.now().plusYears(100)); // 长期锁定
        userMapper.updateById(user);
    }

    @Override
    @OperationLog(value = "解锁用户", type = "update")
    public void unlockUser(Long id) {
        User user = userMapper.selectById(id);
        if (user == null) throw new BusinessException("用户不存在");
        user.setLockedAt(null);
        user.setLoginAttempts(0);
        userMapper.updateById(user);
    }

    @Override
    @OperationLog(value = "管理员重置密码", type = "update")
    public void adminResetPassword(Long id) {
        User user = userMapper.selectById(id);
        if (user == null) throw new BusinessException("用户不存在");
        user.setPassword(passwordEncoder.encode("123456"));
        user.setLoginAttempts(0);
        user.setLockedAt(null);
        userMapper.updateById(user);
    }
}
