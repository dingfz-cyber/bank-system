package com.bank.module.transaction;

import com.bank.common.NotLoginException;
import com.bank.common.PageResult;
import com.bank.common.Result;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.math.BigDecimal;
import java.util.List;
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
     * 导出交易流水 CSV
     */
    @GetMapping("/export")
    public void export(@RequestParam(required = false) String startDate,
                       @RequestParam(required = false) String endDate,
                       HttpServletRequest request, HttpServletResponse response) throws IOException {
        Long userId = getUserId(request);
        response.setContentType("text/csv;charset=UTF-8");
        response.setHeader("Content-Disposition", "attachment;filename=transaction.csv");
        // BOM for Excel UTF-8
        response.getOutputStream().write(new byte[]{(byte)0xEF, (byte)0xBB, (byte)0xBF});
        PrintWriter w = response.getWriter();
        w.println("时间,类型,金额,交易对方,备注");
        transactionService.list(userId, 1, 10000, null, startDate, endDate).getRecords().forEach(r -> {
            String type = switch (r.getType()) { case 1->"转账"; case 2->"缴费"; case 3->"理财"; case 4->"还款"; default->"-"; };
            w.printf("%s,%s,%.2f,%s,%s\n", r.getTradeTime(), type, r.getAmount(),
                r.getToAccount()!=null&&r.getToAccount().length()>=4?"****"+r.getToAccount().substring(r.getToAccount().length()-4):"-",
                r.getRemark()!=null?r.getRemark():"");
        });
        w.flush();
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
