package com.flashsale.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class InventoryTransferDTO {
    @NotNull(message = "库存ID不能为空")
    private Long inventoryId;

    @NotNull(message = "目标用户ID不能为空")
    private Long targetUserId;
}
