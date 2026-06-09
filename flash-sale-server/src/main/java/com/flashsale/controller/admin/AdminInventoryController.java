package com.flashsale.controller.admin;

import com.flashsale.common.Result;
import com.flashsale.dto.InventoryTransferDTO;
import com.flashsale.service.InventoryService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

@RestController
@RequestMapping("/api/admin")
public class AdminInventoryController {

    @Resource
    private InventoryService inventoryService;

    /** 调配库存（A→B） */
    @PutMapping("/inventory/transfer")
    public Result<?> transfer(@Valid @RequestBody InventoryTransferDTO dto) {
        inventoryService.transferInventory(dto.getInventoryId(), dto.getTargetUserId());
        return Result.ok("调配成功");
    }

    /** 删除库存 */
    @DeleteMapping("/inventory/{id}")
    public Result<?> deleteInventory(@PathVariable Long id) {
        inventoryService.deleteInventory(id);
        return Result.ok("删除成功");
    }
}
