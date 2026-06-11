package com.bank.module.transaction;

import com.bank.common.NotLoginException;
import com.bank.common.PageResult;
import com.bank.common.Result;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

/**
 * 交易控制器
 */
@RestController
@RequestMapping("/transaction")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    private Long getUserId(HttpServletRequest request) {
        Object attr = request.getAttribute("userId");
        if (attr == null) throw new NotLoginException("未登录");
        return Long.parseLong(attr.toString());
    }

    /**
     * 行内转账
     */
    @PostMapping("/transfer")
    public Result<Void> transfer(@RequestBody Map<String, Object> body, HttpServletRequest request) {
        Long userId = getUserId(request);
        Long fromCardId = Long.parseLong(body.get("fromCardId").toString());
        String toAccount = body.get("toAccount").toString();
        BigDecimal amount = new BigDecimal(body.get("amount").toString());
        String transactionPassword = body.get("transactionPassword").toString();
        String remark = body.getOrDefault("remark", "").toString();
        transactionService.transfer(userId, fromCardId, toAccount, amount, transactionPassword, remark);
        return Result.success();
    }

    /**
     * 交易流水查询
     */
    @GetMapping("/list")
    public Result<PageResult<TransactionRecord>> list(
            @RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "10") long pageSize,
            @RequestParam(required = false) Integer type,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            HttpServletRequest request) {
        Long userId = getUserId(request);
        return Result.success(PageResult.of(transactionService.list(userId, page, pageSize, type, startDate, endDate)));
    }
}
