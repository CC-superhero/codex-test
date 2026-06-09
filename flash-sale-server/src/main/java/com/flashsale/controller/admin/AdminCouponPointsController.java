package com.flashsale.controller.admin;

import com.flashsale.common.Result;
import com.flashsale.dto.GrantCouponDTO;
import com.flashsale.dto.GrantPointsDTO;
import com.flashsale.entity.Coupon;
import com.flashsale.entity.PointsLog;
import com.flashsale.entity.User;
import com.flashsale.service.CouponService;
import com.flashsale.service.PointsService;
import com.flashsale.service.UserService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("/api/admin")
public class AdminCouponPointsController {

    @Resource
    private CouponService couponService;
    @Resource
    private PointsService pointsService;
    @Resource
    private UserService userService;

    /** 发放优惠券 */
    @PostMapping("/coupon/grant")
    public Result<?> grantCoupon(@Valid @RequestBody GrantCouponDTO dto) {
        Coupon coupon = new Coupon();
        coupon.setUserId(dto.getUserId());
        coupon.setName(dto.getName());
        coupon.setFaceValue(dto.getFaceValue());
        coupon.setExpireTime(LocalDateTime.parse(dto.getExpireTime(),
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        coupon.setStatus(0);
        couponService.save(coupon);
        return Result.ok("发放成功");
    }

    /** 发放积分 */
    @PostMapping("/points/grant")
    public Result<?> grantPoints(@Valid @RequestBody GrantPointsDTO dto) {
        // 更新用户积分
        User user = userService.getById(dto.getUserId());
        BigDecimal newPoints = user.getPoints().add(dto.getAmount());
        user.setPoints(newPoints);
        userService.updateById(user);

        // 记录积分变动
        PointsLog log = new PointsLog();
        log.setUserId(dto.getUserId());
        log.setChangeAmount(dto.getAmount());
        log.setType(4); // 后台发放
        log.setRemark(dto.getRemark());
        pointsService.save(log);

        return Result.ok("发放成功");
    }
}
