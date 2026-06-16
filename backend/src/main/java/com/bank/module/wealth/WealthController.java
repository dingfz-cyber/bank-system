package com.bank.module.wealth;

import com.bank.common.*;
import com.bank.module.card.*;
import com.bank.module.points.PointsRecord;
import com.bank.module.points.PointsRecordMapper;
import com.bank.module.transaction.TransactionMapper;
import com.bank.module.transaction.TransactionRecord;
import com.bank.module.user.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/wealth")
@RequiredArgsConstructor
public class WealthController {

    private final WealthHoldingMapper holdingMapper;
    private final BankCardMapper cardMapper;
    private final UserMapper userMapper;
    private final PointsRecordMapper pointsRecordMapper;
    private final TransactionMapper transactionMapper;

    private Long getUserId(HttpServletRequest r){Object a=r.getAttribute("userId");if(a==null)throw new NotLoginException("未登录");return Long.parseLong(a.toString());}

    /** 我的理财持仓 */
    @GetMapping("/holdings")
    public Result<List<Map<String,Object>>> holdings(HttpServletRequest r){
        Long uid=getUserId(r);
        List<Map<String,Object>> list=holdingMapper.selectByUserId(uid);
        // 模拟每日计息: dailyEarn = amount * rate/100 / 365
        list.forEach(m->{
            BigDecimal amt=(BigDecimal)m.getOrDefault("amount",BigDecimal.ZERO);
            java.math.RoundingMode rm=java.math.RoundingMode.HALF_UP;
            Object rateObj=m.get("rate");
            BigDecimal rate=rateObj!=null?new BigDecimal(rateObj.toString()):BigDecimal.ZERO;
            LocalDateTime buyTime=(LocalDateTime)m.get("buy_time");
            BigDecimal days=new BigDecimal(LocalDateTime.now().getDayOfYear()-buyTime.getDayOfYear()+1);
            m.put("dailyEarn",amt.multiply(rate).divide(new BigDecimal("36500"),2,rm).multiply(days));
            m.put("totalValue",amt.add((BigDecimal)m.get("dailyEarn")));
        });
        return Result.success(list);
    }

    /** 购买理财产品 */
    @PostMapping("/buy")
    public Result<Void> buy(@RequestBody Map<String,Object> body, HttpServletRequest r){
        Long uid=getUserId(r); Long cardId=Long.parseLong(body.get("cardId").toString());
        Long productId=Long.parseLong(body.get("productId").toString());
        String productName=body.get("productName").toString();
        BigDecimal amount=new BigDecimal(body.get("amount").toString());
        BigDecimal rate=new BigDecimal(body.get("rate").toString());

        BankCard card=cardMapper.selectById(cardId);
        if(card==null||!card.getUserId().equals(uid)) throw new BusinessException("银行卡不存在");
        if(card.getBalance().compareTo(amount)<0) throw new BusinessException("余额不足");

        card.setBalance(card.getBalance().subtract(amount)); cardMapper.updateById(card);
        holdingMapper.insert(uid,cardId,productId,productName,amount,rate);
        // 交易流水
        TransactionRecord tr=new TransactionRecord(); tr.setUserId(uid); tr.setFromCardId(cardId);
        tr.setToAccount(productName); tr.setAmount(amount); tr.setFee(BigDecimal.ZERO); tr.setType(3);
        tr.setStatus(1); tr.setTradeTime(LocalDateTime.now()); tr.setRemark("购买理财产品");
        transactionMapper.insert(tr);

        // 积分+20
        User u=userMapper.selectById(uid);
        if(u!=null&&u.getRoleId()!=null&&u.getRoleId()==5){
            u.setPoints((u.getPoints()==null?0:u.getPoints())+20); userMapper.updateById(u);
            PointsRecord pr=new PointsRecord(); pr.setUserId(uid); pr.setPoints(20); pr.setReason("购买理财产品"); pointsRecordMapper.insert(pr);
        }
        return Result.success();
    }
}
