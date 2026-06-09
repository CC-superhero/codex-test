package com.flashsale.controller.user;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.flashsale.common.BizException;
import com.flashsale.common.Result;
import com.flashsale.entity.Product;
import com.flashsale.service.ProductLifecycleService;
import com.flashsale.service.ProductService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/api/user")
public class ProductController {

    @Resource
    private ProductService productService;
    @Resource
    private ProductLifecycleService productLifecycleService;

    @GetMapping("/product/list")
    public Result<Page<Product>> list(@RequestParam(defaultValue = "1") Integer page,
                                      @RequestParam(defaultValue = "20") Integer size) {
        productLifecycleService.refreshAllProductStatus();
        Page<Product> p = new Page<>(page, size);
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<Product>()
                .in(Product::getStatus, 0, 1)
                .orderByAsc(Product::getStartTime)
                .orderByDesc(Product::getCreateTime);
        return Result.ok(productService.page(p, wrapper));
    }

    @GetMapping("/product/{id}")
    public Result<Product> detail(@PathVariable Long id) {
        Product product = productService.getById(id);
        if (product == null) {
            throw new BizException("商品不存在");
        }
        productLifecycleService.syncProductStatus(product);
        return Result.ok(product);
    }
}
