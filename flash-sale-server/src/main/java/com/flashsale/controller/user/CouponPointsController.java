package com.flashsale.controller.user;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.flashsale.common.Result;
import com.flashsale.entity.Coupon;
import com.flashsale.entity.PointsLog;
import com.flashsale.entity.User;
import com.flashsale.service.CouponService;
import com.flashsale.service.PointsService;
import com.flashsale.service.UserService;
import com.flashsale.util.SecurityUtil;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class CouponPointsController {

    @Resource
    private CouponService couponService;
    @Resource
    private PointsService pointsService;
    @Resource
    private UserService userService;

    @GetMapping("/coupon/list")
    public Result<List<Coupon>> myCoupons() {
        Long userId = SecurityUtil.getCurrentUserId();
        List<Coupon> list = couponService.list(
                new LambdaQueryWrapper<Coupon>()
                        .eq(Coupon::getUserId, userId)
                        .orderByDesc(Coupon::getCreateTime));
        return Result.ok(list);
    }

    @GetMapping("/points/log")
    public Result<List<PointsLog>> pointsLog() {
        Long userId = SecurityUtil.getCurrentUserId();
        List<PointsLog> list = pointsService.list(
                new LambdaQueryWrapper<PointsLog>()
                        .eq(PointsLog::getUserId, userId)
                        .orderByDesc(PointsLog::getCreateTime));
        return Result.ok(list);
    }

    @GetMapping("/invite/info")
    public Result<Map<String, Object>> inviteInfo() {
        Long userId = SecurityUtil.getCurrentUserId();
        User user = userService.getById(userId);

        long inviteCount = userService.count(
                new LambdaQueryWrapper<User>().eq(User::getParentId, userId));

        Map<String, Object> result = new HashMap<>();
        result.put("inviteCode", user.getInviteCode());
        result.put("inviteCount", inviteCount);
        return Result.ok(result);
    }
}
