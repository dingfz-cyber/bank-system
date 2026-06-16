package com.bank.module.utility;

import com.bank.common.*;
import com.bank.module.card.*;
import com.bank.module.transaction.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/utility")
@RequiredArgsConstructor
public class UtilityController {

    private final UtilityMapper utilityMapper;
    private final BankCardMapper cardMapper;
    private final TransactionMapper transactionMapper;

    private Long getUserId(HttpServletRequest r){Object a=r.getAttribute("userId");if(a==null)throw new NotLoginException("未登录");return Long.parseLong(a.toString());}

    /** 我的户号列表 */
    @GetMapping("/accounts")
    public Result<List<Map<String,Object>>> accounts(HttpServletRequest r){
        return Result.success(utilityMapper.selectByUser(getUserId(r)));
    }

    /** 查询账单 */
    @GetMapping("/query")
    public Result<Map<String,Object>> query(@RequestParam String accountNo, @RequestParam String type){
        Map<String,Object> a=utilityMapper.findByAccount(accountNo,type);
        if(a==null) return Result.error(404,"户号不存在");
        a.put("billAmount",100+new Random().nextInt(400)); a.put("billPeriod","2026-06");
        a.put("dueDate","2026-07-15"); a.put("address","模拟地址"+new Random().nextInt(100)+"号");
        return Result.success(a);
    }

    /** 缴费支付 */
    @PostMapping("/pay")
    public Result<Void> pay(@RequestBody Map<String,Object> body, HttpServletRequest r){
        Long uid=getUserId(r); Long cardId=Long.parseLong(body.get("cardId").toString());
        String accountNo=body.get("accountNo").toString(); String type=body.get("type").toString();
        BigDecimal amount=new BigDecimal(body.get("amount").toString());

        BankCard card=cardMapper.selectById(cardId);
        if(card==null||!card.getUserId().equals(uid)) throw new BusinessException("银行卡不存在");
        if(card.getBalance().compareTo(amount)<0) throw new BusinessException("余额不足");
        card.setBalance(card.getBalance().subtract(amount)); cardMapper.updateById(card);

        // 更新户号余额
        utilityMapper.payBill(accountNo,type,amount);

        TransactionRecord tr=new TransactionRecord(); tr.setUserId(uid); tr.setFromCardId(cardId);
        tr.setToAccount(accountNo); tr.setAmount(amount); tr.setFee(BigDecimal.ZERO); tr.setType(2);
        tr.setStatus(1); tr.setTradeTime(LocalDateTime.now()); tr.setRemark(type+"缴费");
        transactionMapper.insert(tr);
        return Result.success();
    }
}
