package com.bank.module.card;

import com.bank.common.NotLoginException;
import com.bank.common.Result;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 银行卡控制器
 */
@RestController
@RequestMapping("/card")
@RequiredArgsConstructor
public class BankCardController {

    private final BankCardService bankCardService;

    private Long getUserId(HttpServletRequest request) {
        Object attr = request.getAttribute("userId");
        if (attr == null) throw new NotLoginException("未登录");
        return Long.parseLong(attr.toString());
    }

    /**
     * 我的银行卡列表
     */
    @GetMapping("/list")
    public Result<List<BankCard>> list(HttpServletRequest request) {
        Long userId = getUserId(request);
        return Result.success(bankCardService.listByUserId(userId));
    }

    /**
     * 银行卡详情
     */
    @GetMapping("/detail/{id}")
    public Result<BankCard> detail(@PathVariable Long id) {
        return Result.success(bankCardService.detail(id));
    }

    /**
     * 冻结银行卡
     */
    @PostMapping("/freeze/{id}")
    public Result<Void> freeze(@PathVariable Long id, HttpServletRequest request) {
        Long userId = getUserId(request);
        bankCardService.freeze(id, userId);
        return Result.success();
    }

    /**
     * 解冻银行卡
     */
    @PostMapping("/unfreeze/{id}")
    public Result<Void> unfreeze(@PathVariable Long id, HttpServletRequest request) {
        Long userId = getUserId(request);
        bankCardService.unfreeze(id, userId);
        return Result.success();
    }

    /**
     * 注销银行卡
     */
    @PostMapping("/cancel/{id}")
    public Result<Void> cancel(@PathVariable Long id, HttpServletRequest request) {
        Long userId = getUserId(request);
        bankCardService.cancel(id, userId);
        return Result.success();
    }
}
