package com.bank.module.wealth;

import org.apache.ibatis.annotations.*;
import java.math.BigDecimal;
import java.util.*;

@Mapper
public interface WealthHoldingMapper {
    @Select("SELECT h.*,p.rate FROM wealth_holding h LEFT JOIN bank_product p ON h.product_id=p.id WHERE h.user_id=#{userId} AND h.status=0 ORDER BY h.buy_time DESC")
    List<Map<String,Object>> selectByUserId(@Param("userId") Long userId);

    @Insert("INSERT INTO wealth_holding(user_id,card_id,product_id,product_name,amount,rate) VALUES(#{userId},#{cardId},#{productId},#{productName},#{amount},#{rate})")
    void insert(@Param("userId") Long userId, @Param("cardId") Long cardId, @Param("productId") Long pid,
                @Param("productName") String name, @Param("amount") BigDecimal amt, @Param("rate") BigDecimal rate);
}
