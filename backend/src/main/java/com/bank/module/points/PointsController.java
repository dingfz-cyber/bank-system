package com.bank.module.points;

import com.bank.common.NotLoginException;
import com.bank.common.Result;
import com.bank.rbac.RequireRole;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/points")
@RequiredArgsConstructor
public class PointsController {

    private final PointsMapper pointsMapper;
    private final PointsRecordMapper recordMapper;

    private Long getUserId(HttpServletRequest r) { Object a=r.getAttribute("userId"); if(a==null) throw new NotLoginException("未登录"); return Long.parseLong(a.toString()); }

    /** 积分商城商品列表 */
    @GetMapping("/mall")
    public Result<List<Map<String,Object>>> mall() {
        return Result.success(pointsMapper.selectAllGoods());
    }

    /** 积分获取记录 */
    @GetMapping("/records")
    public Result<List<PointsRecord>> records(HttpServletRequest r) {
        Long uid = getUserId(r);
        return Result.success(recordMapper.selectList(
            new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<PointsRecord>()
                .eq(PointsRecord::getUserId, uid).orderByDesc(PointsRecord::getCreateTime).last("LIMIT 50")));
    }

    /** 兑换商品 */
    @PostMapping("/redeem/{goodsId}")
    public Result<Void> redeem(@PathVariable Long goodsId, HttpServletRequest r) {
        Long uid = getUserId(r);
        pointsMapper.redeem(uid, goodsId);
        return Result.success();
    }

    /** 管理员管理商品 */
    @GetMapping("/admin/goods")
    @RequireRole("BIZ_ADMIN")
    public Result<List<Map<String,Object>>> adminGoods() {
        return Result.success(pointsMapper.selectAllGoods());
    }

    @PostMapping("/admin/goods")
    @RequireRole("BIZ_ADMIN")
    public Result<Void> addGoods(@RequestBody Map<String,Object> body) {
        pointsMapper.insertGoods(body.get("name").toString(), Integer.parseInt(body.get("cost").toString()),
            body.get("image").toString(), Integer.parseInt(body.get("stock").toString()));
        return Result.success();
    }

    @PutMapping("/admin/goods/{id}")
    @RequireRole("BIZ_ADMIN")
    public Result<Void> updateGoods(@PathVariable Long id, @RequestBody Map<String,Object> body) {
        pointsMapper.updateGoods(id, body.get("name").toString(), Integer.parseInt(body.get("cost").toString()),
            body.get("image").toString(), Integer.parseInt(body.get("stock").toString()));
        return Result.success();
    }

    @DeleteMapping("/admin/goods/{id}")
    @RequireRole("BIZ_ADMIN")
    public Result<Void> deleteGoods(@PathVariable Long id) {
        pointsMapper.deleteGoods(id);
        return Result.success();
    }
}
