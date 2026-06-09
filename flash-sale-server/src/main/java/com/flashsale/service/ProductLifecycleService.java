package com.flashsale.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.flashsale.entity.Product;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ProductLifecycleService {

    private static final String STOCK_KEY_PREFIX = "flash:stock:";

    @Resource
    private ProductService productService;
    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Scheduled(fixedDelay = 30000)
    public void refreshAllProductStatus() {
        List<Product> products = productService.list(new LambdaQueryWrapper<Product>()
                .orderByAsc(Product::getStartTime));
        for (Product product : products) {
            syncProductStatus(product);
        }
    }

    public void syncProductStatus(Long productId) {
        Product product = productService.getById(productId);
        if (product != null) {
            syncProductStatus(product);
        }
    }

    public void syncProductStatus(Product product) {
        Integer targetStatus = calculateStatus(product, LocalDateTime.now());
        boolean changed = product.getStatus() == null || !product.getStatus().equals(targetStatus);
        if (changed) {
            product.setStatus(targetStatus);
            productService.updateById(product);
        }

        if (targetStatus == 1) {
            ensureStockCache(product);
        }
    }

    public Integer calculateStatus(Product product, LocalDateTime now) {
        if (product.getRemainStock() == null || product.getRemainStock() <= 0) {
            return 2;
        }
        if (product.getStartTime() != null && now.isBefore(product.getStartTime())) {
            return 0;
        }
        if (product.getEndTime() != null && !now.isBefore(product.getEndTime())) {
            return 2;
        }
        return 1;
    }

    private void ensureStockCache(Product product) {
        String stockKey = STOCK_KEY_PREFIX + product.getId();
        if (!Boolean.TRUE.equals(redisTemplate.hasKey(stockKey))) {
            redisTemplate.opsForValue().set(stockKey, product.getRemainStock());
        }
    }
}
