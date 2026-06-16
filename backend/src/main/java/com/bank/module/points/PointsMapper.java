package com.bank.module.points;

import org.apache.ibatis.annotations.*;
import java.util.*;

@Mapper
public interface PointsMapper {

    @Select("SELECT id,name,cost,image,stock FROM points_goods WHERE deleted=0 ORDER BY cost")
    List<Map<String,Object>> selectAllGoods();

    @Update("UPDATE sys_user SET points=points-${cost} WHERE id=#{userId} AND points>=${cost}")
    @Options(keyProperty="cost")
    int deductPoints(@Param("userId") Long userId, @Param("cost") int cost);

    @Update("UPDATE points_goods SET stock=stock-1 WHERE id=#{goodsId} AND stock>0 AND deleted=0")
    int reduceStock(@Param("goodsId") Long goodsId);

    default void redeem(Long userId, Long goodsId) {
        Map<String,Object> g = selectAllGoods().stream().filter(m->m.get("id").equals(goodsId)).findFirst().orElse(null);
        if(g==null) throw new RuntimeException("商品不存在");
        int cost = (Integer)g.get("cost");
        if(deductPoints(userId,cost)==0) throw new RuntimeException("积分不足");
        reduceStock(goodsId);
    }

    @Insert("INSERT INTO points_goods(name,cost,image,stock) VALUES(#{name},#{cost},#{image},#{stock})")
    void insertGoods(@Param("name") String n, @Param("cost") int c, @Param("image") String img, @Param("stock") int s);

    @Update("UPDATE points_goods SET name=#{name},cost=#{cost},image=#{image},stock=#{stock} WHERE id=#{id}")
    void updateGoods(@Param("id") Long id, @Param("name") String n, @Param("cost") int c, @Param("image") String img, @Param("stock") int s);

    @Update("UPDATE points_goods SET deleted=1 WHERE id=#{id}")
    void deleteGoods(@Param("id") Long id);
}
