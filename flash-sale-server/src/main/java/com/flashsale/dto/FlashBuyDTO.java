package com.flashsale.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class FlashBuyDTO {
    @NotNull(message = "商品ID不能为空")
    private Long productId;
    private Long couponId;   // 可选，使用的优惠券ID
    private Double pointsUsed; // 可选，使用的积分数
}
