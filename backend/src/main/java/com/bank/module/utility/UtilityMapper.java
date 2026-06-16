package com.bank.module.utility;

import org.apache.ibatis.annotations.*;
import java.math.BigDecimal;
import java.util.*;

@Mapper
public interface UtilityMapper {
    @Select("SELECT id,type,account_no,holder_name,address,balance FROM utility_account WHERE user_id=#{uid} AND deleted=0")
    List<Map<String,Object>> selectByUser(@Param("uid") Long uid);

    @Select("SELECT * FROM utility_account WHERE account_no=#{no} AND type=#{type} AND deleted=0 LIMIT 1")
    Map<String,Object> findByAccount(@Param("no") String no, @Param("type") String type);

    @Update("UPDATE utility_account SET balance=balance+#{amt} WHERE account_no=#{no} AND type=#{type}")
    void payBill(@Param("no") String no, @Param("type") String type, @Param("amt") BigDecimal amt);
}
