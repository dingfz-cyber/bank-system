package com.bank.module.transaction;

import com.bank.common.NotLoginException;
import com.bank.common.Result;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/payee")
@RequiredArgsConstructor
public class PayeeController {

    private final PayeeMapper payeeMapper;

    private Long getUserId(HttpServletRequest r) {
        Object a = r.getAttribute("userId");
        if (a == null) throw new NotLoginException("未登录");
        return Long.parseLong(a.toString());
    }

    @GetMapping("/list")
    public Result<List<Payee>> list(HttpServletRequest r) {
        Long uid = getUserId(r);
        List<Payee> list = payeeMapper.selectList(
            new LambdaQueryWrapper<Payee>().eq(Payee::getUserId, uid).orderByDesc(Payee::getCreateTime));
        return Result.success(list);
    }

    @PostMapping("/add")
    public Result<Void> add(@RequestBody Payee payee, HttpServletRequest r) {
        payee.setUserId(getUserId(r));
        payeeMapper.insert(payee);
        return Result.success();
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody Payee payee, HttpServletRequest r) {
        payee.setId(id);
        payeeMapper.updateById(payee);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        payeeMapper.deleteById(id);
        return Result.success();
    }
}
