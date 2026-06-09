package com.flashsale.controller.user;

import com.flashsale.common.Result;
import com.flashsale.dto.FlashBuyDTO;
import com.flashsale.service.FlashSaleService;
import com.flashsale.util.SecurityUtil;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class FlashSaleController {

    @Resource
    private FlashSaleService flashSaleService;

    @PostMapping("/flash/buy")
    public Result<?> flashBuy(@Valid @RequestBody FlashBuyDTO dto) {
        Long userId = SecurityUtil.getCurrentUserId();
        BigDecimal pointsUsed = dto.getPointsUsed() != null
                ? BigDecimal.valueOf(dto.getPointsUsed()) : BigDecimal.ZERO;
        Long orderId = flashSaleService.buy(userId, dto.getProductId(), dto.getCouponId(), pointsUsed);
        Map<String, Object> data = new HashMap<>();
        data.put("orderId", orderId);
        return Result.ok("抢购提交成功，订单处理中", data);
    }
}
