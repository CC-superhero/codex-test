package com.flashsale.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
public class GrantPointsDTO {
    @NotNull(message = "用户ID不能为空")
    private Long userId;

    @NotNull(message = "积分数不能为空")
    private BigDecimal amount;

    @NotBlank(message = "备注不能为空")
    private String remark;
}
