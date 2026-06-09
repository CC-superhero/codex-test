package com.flashsale.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.flashsale.common.Result;
import com.flashsale.dto.ProductPublishDTO;
import com.flashsale.entity.Product;
import com.flashsale.service.ProductLifecycleService;
import com.flashsale.service.ProductService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("/api/admin")
public class AdminProductController {

    private static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Resource
    private ProductService productService;
    @Resource
    private ProductLifecycleService productLifecycleService;

    @GetMapping("/product/list")
    public Result<Page<Product>> productList(@RequestParam(defaultValue = "1") Integer page,
                                             @RequestParam(defaultValue = "20") Integer size) {
        productLifecycleService.refreshAllProductStatus();
        Page<Product> p = new Page<>(page, size);
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<Product>()
                .orderByDesc(Product::getCreateTime);
        return Result.ok(productService.page(p, wrapper));
    }

    @GetMapping("/product/{id}")
    public Result<Product> detail(@PathVariable Long id) {
        Product product = productService.getById(id);
        if (product != null) {
            productLifecycleService.syncProductStatus(product);
        }
        return Result.ok(product);
    }

    @PostMapping("/product")
    public Result<?> publish(@Valid @RequestBody ProductPublishDTO dto) {
        Product product = buildProduct(dto);
        product.setRemainStock(dto.getTotalStock());
        productService.save(product);
        productLifecycleService.syncProductStatus(product.getId());
        return Result.ok();
    }

    @PutMapping("/product/{id}")
    public Result<?> updateProduct(@PathVariable Long id, @Valid @RequestBody ProductPublishDTO dto) {
        Product existing = productService.getById(id);
        if (existing == null) {
            return Result.fail("商品不存在");
        }

        Product product = buildProduct(dto);
        product.setId(id);
        product.setRemainStock(resolveRemainStock(existing, dto.getTotalStock()));
        productService.updateById(product);
        productLifecycleService.syncProductStatus(id);
        return Result.ok();
    }

    @DeleteMapping("/product/{id}")
    public Result<?> deleteProduct(@PathVariable Long id) {
        productService.removeById(id);
        return Result.ok();
    }

    private Product buildProduct(ProductPublishDTO dto) {
        Product product = new Product();
        product.setName(dto.getName());
        product.setPrice(dto.getPrice());
        product.setImage(dto.getImage());
        product.setTotalStock(dto.getTotalStock());
        product.setStartTime(LocalDateTime.parse(dto.getStartTime(), DATE_TIME_FORMATTER));
        product.setEndTime(LocalDateTime.parse(dto.getEndTime(), DATE_TIME_FORMATTER));
        product.setStatus(0);
        return product;
    }

    private Integer resolveRemainStock(Product existing, Integer newTotalStock) {
        int soldCount = Math.max(0, existing.getTotalStock() - existing.getRemainStock());
        return Math.max(newTotalStock - soldCount, 0);
    }
}
