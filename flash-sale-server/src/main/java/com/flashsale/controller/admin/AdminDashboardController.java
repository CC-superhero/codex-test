package com.flashsale.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.flashsale.common.Result;
import com.flashsale.entity.Order;
import com.flashsale.entity.Product;
import com.flashsale.entity.UserInventory;
import com.flashsale.mapper.OrderMapper;
import com.flashsale.service.InventoryService;
import com.flashsale.service.ProductLifecycleService;
import com.flashsale.service.ProductService;
import com.flashsale.service.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/dashboard")
public class AdminDashboardController {

    @Resource
    private UserService userService;
    @Resource
    private ProductService productService;
    @Resource
    private InventoryService inventoryService;
    @Resource
    private ProductLifecycleService productLifecycleService;
    @Resource
    private OrderMapper orderMapper;

    @GetMapping("/stats")
    public Result<Map<String, Object>> stats() {
        productLifecycleService.refreshAllProductStatus();
        LocalDateTime todayStart = LocalDate.now().atStartOfDay();

        Map<String, Object> stats = new HashMap<>();
        stats.put("userCount", userService.count());
        stats.put("productCount", productService.count());
        stats.put("activeProductCount", productService.count(new LambdaQueryWrapper<Product>()
                .eq(Product::getStatus, 1)));
        stats.put("orderCount", orderMapper.selectCount(new LambdaQueryWrapper<Order>()
                .eq(Order::getStatus, 1)
                .ge(Order::getCreateTime, todayStart)));
        stats.put("inventoryCount", inventoryService.count());
        stats.put("listedInventoryCount", inventoryService.count(new LambdaQueryWrapper<UserInventory>()
                .eq(UserInventory::getStatus, 2)));
        return Result.ok(stats);
    }
}
