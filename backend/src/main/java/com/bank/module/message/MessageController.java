package com.bank.module.message;

import com.bank.common.NotLoginException;
import com.bank.common.Result;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/message")
@RequiredArgsConstructor
public class MessageController {

    private final MessageMapper messageMapper;

    private Long getUserId(HttpServletRequest r) {
        Object a = r.getAttribute("userId");
        if (a == null) throw new NotLoginException("未登录");
        return Long.parseLong(a.toString());
    }

    @GetMapping("/list")
    public Result<List<Message>> list(HttpServletRequest r) {
        Long uid = getUserId(r);
        List<Message> list = messageMapper.selectList(
            new LambdaQueryWrapper<Message>().eq(Message::getUserId, uid)
                .orderByDesc(Message::getCreateTime).last("LIMIT 50"));
        return Result.success(list);
    }

    @GetMapping("/unread-count")
    public Result<Map<String,Integer>> unreadCount(HttpServletRequest r) {
        return Result.success(Map.of("count", messageMapper.countUnread(getUserId(r))));
    }

    @PostMapping("/read/{id}")
    public Result<Void> read(@PathVariable Long id) {
        messageMapper.update(null, new LambdaUpdateWrapper<Message>().eq(Message::getId, id).set(Message::getIsRead, 1));
        return Result.success();
    }

    @PostMapping("/read-all")
    public Result<Void> readAll(HttpServletRequest r) {
        messageMapper.update(null, new LambdaUpdateWrapper<Message>()
            .eq(Message::getUserId, getUserId(r)).set(Message::getIsRead, 1));
        return Result.success();
    }
}
