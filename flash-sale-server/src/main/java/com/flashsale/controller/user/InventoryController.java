package com.flashsale.controller.user;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.flashsale.common.Result;
import com.flashsale.dto.ListProductDTO;
import com.flashsale.entity.UserInventory;
import com.flashsale.service.InventoryService;
import com.flashsale.util.SecurityUtil;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/user")
public class InventoryController {

    @Resource
    private InventoryService inventoryService;

    @GetMapping("/inventory/list")
    public Result<List<UserInventory>> myInventory() {
        Long userId = SecurityUtil.getCurrentUserId();
        List<UserInventory> list = inventoryService.list(new LambdaQueryWrapper<UserInventory>()
                .eq(UserInventory::getUserId, userId)
                .eq(UserInventory::getStatus, 1)
                .orderByDesc(UserInventory::getCreateTime));
        return Result.ok(list);
    }

    @GetMapping("/inventory/seller")
    public Result<List<UserInventory>> sellerInventory() {
        Long userId = SecurityUtil.getCurrentUserId();
        List<UserInventory> list = inventoryService.list(new LambdaQueryWrapper<UserInventory>()
                .eq(UserInventory::getUserId, userId)
                .eq(UserInventory::getStatus, 2)
                .orderByDesc(UserInventory::getCreateTime));
        return Result.ok(list);
    }

    @PostMapping("/inventory/list")
    public Result<?> listProduct(@Valid @RequestBody ListProductDTO dto) {
        Long userId = SecurityUtil.getCurrentUserId();
        inventoryService.listProduct(userId, dto.getInventoryId());
        return Result.ok("上架成功");
    }

    @DeleteMapping("/inventory/list/{id}")
    public Result<?> delistProduct(@PathVariable Long id) {
        Long userId = SecurityUtil.getCurrentUserId();
        inventoryService.delistProduct(userId, id);
        return Result.ok("下架成功");
    }

    @GetMapping("/market/list")
    public Result<List<UserInventory>> marketInventory() {
        Long userId = SecurityUtil.getCurrentUserId();
        return Result.ok(inventoryService.marketInventory(userId));
    }

    @PostMapping("/market/buy/{id}")
    public Result<?> buyListedProduct(@PathVariable Long id) {
        Long userId = SecurityUtil.getCurrentUserId();
        inventoryService.buyListedProduct(userId, id);
        return Result.ok("购买成功");
    }
}
