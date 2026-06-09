package com.flashsale.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class ListProductDTO {
    @NotNull(message = "库存ID不能为空")
    private Long inventoryId;
}
