package com.flashsale.config;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.flashsale.entity.Admin;
import com.flashsale.entity.Product;
import com.flashsale.mapper.AdminMapper;
import com.flashsale.mapper.ProductMapper;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Component
public class DataInitializer implements CommandLineRunner {

    @Resource
    private AdminMapper adminMapper;
    @Resource
    private ProductMapper productMapper;
    @Resource
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        initAdmin();
        initProducts();
    }

    private void initAdmin() {
        Long count = adminMapper.selectCount(new LambdaQueryWrapper<>());
        if (count == 0) {
            Admin admin = new Admin();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            adminMapper.insert(admin);
        }
    }

    private void initProducts() {
        Long count = productMapper.selectCount(new LambdaQueryWrapper<>());
        if (count == 0) {
            LocalDateTime now = LocalDateTime.now();
            insertProduct("iPhone 15", new BigDecimal("6999.00"),
                    now.plusDays(1), now.plusDays(7), 100);
            insertProduct("MacBook Pro", new BigDecimal("14999.00"),
                    now.plusDays(2), now.plusDays(10), 50);
            insertProduct("AirPods Pro", new BigDecimal("1999.00"),
                    now.minusDays(1), now.plusDays(5), 200);
            insertProduct("iPad Air", new BigDecimal("4999.00"),
                    now.plusDays(3), now.plusDays(14), 80);
        }
    }

    private void insertProduct(String name, BigDecimal price,
                               LocalDateTime start, LocalDateTime end, int stock) {
        Product p = new Product();
        p.setName(name);
        p.setPrice(price);
        p.setTotalStock(stock);
        p.setRemainStock(stock);
        p.setStartTime(start);
        p.setEndTime(end);
        p.setStatus(0);
        productMapper.insert(p);
    }
}
