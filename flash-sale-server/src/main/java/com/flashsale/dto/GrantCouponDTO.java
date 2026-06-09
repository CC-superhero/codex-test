package com.flashsale.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
public class GrantCouponDTO {
    @NotNull(message = "用户ID不能为空")
    private Long userId;

    @NotBlank(message = "券名称不能为空")
    private String name;

    @NotNull(message = "面值不能为空")
    private BigDecimal faceValue;

    @NotBlank(message = "有效期不能为空")
    private String expireTime;
}
