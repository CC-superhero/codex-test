package com.flashsale.service;

import com.flashsale.entity.Product;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductLifecycleServiceTest {

    @Mock
    private ProductService productService;
    @Mock
    private RedisTemplate<String, Object> redisTemplate;
    @Mock
    private ValueOperations<String, Object> valueOperations;

    @InjectMocks
    private ProductLifecycleService productLifecycleService;

    @Test
    void calculateStatusShouldReturnEndedWhenStockIsEmpty() {
        Product product = buildProduct(1, null, null, 0);

        Integer status = productLifecycleService.calculateStatus(product, LocalDateTime.now());

        assertEquals(2, status);
    }

    @Test
    void calculateStatusShouldReturnNotStartedBeforeStartTime() {
        LocalDateTime now = LocalDateTime.now();
        Product product = buildProduct(1, now.plusMinutes(5), now.plusHours(1), 10);

        Integer status = productLifecycleService.calculateStatus(product, now);

        assertEquals(0, status);
    }

    @Test
    void calculateStatusShouldReturnEndedAfterEndTime() {
        LocalDateTime now = LocalDateTime.now();
        Product product = buildProduct(1, now.minusHours(2), now.minusMinutes(1), 10);

        Integer status = productLifecycleService.calculateStatus(product, now);

        assertEquals(2, status);
    }

    @Test
    void calculateStatusShouldReturnActiveWithinFlashSaleWindow() {
        LocalDateTime now = LocalDateTime.now();
        Product product = buildProduct(1, now.minusMinutes(10), now.plusMinutes(10), 10);

        Integer status = productLifecycleService.calculateStatus(product, now);

        assertEquals(1, status);
    }

    @Test
    void syncProductStatusShouldUpdateStatusAndWarmupStockWhenCacheMissing() {
        LocalDateTime now = LocalDateTime.now();
        Product product = buildProduct(1, now.minusMinutes(10), now.plusMinutes(10), 10);
        product.setStatus(0);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(redisTemplate.hasKey("flash:stock:1")).thenReturn(false);

        productLifecycleService.syncProductStatus(product);

        assertEquals(1, product.getStatus());
        verify(productService).updateById(product);
        verify(valueOperations).set("flash:stock:1", 10);
    }

    @Test
    void syncProductStatusShouldNotPersistWhenStatusUnchanged() {
        LocalDateTime now = LocalDateTime.now();
        Product product = buildProduct(2, now.minusMinutes(10), now.plusMinutes(10), 10);
        product.setStatus(1);
        when(redisTemplate.hasKey("flash:stock:2")).thenReturn(true);

        productLifecycleService.syncProductStatus(product);

        verify(productService, never()).updateById(any(Product.class));
        verify(valueOperations, never()).set(anyString(), any());
    }

    @Test
    void syncProductStatusByIdShouldIgnoreMissingProduct() {
        when(productService.getById(99L)).thenReturn(null);

        productLifecycleService.syncProductStatus(99L);

        verify(productService).getById(99L);
        verify(productService, never()).updateById(any(Product.class));
    }

    private Product buildProduct(long id, LocalDateTime startTime, LocalDateTime endTime, int remainStock) {
        Product product = new Product();
        product.setId(id);
        product.setStartTime(startTime);
        product.setEndTime(endTime);
        product.setRemainStock(remainStock);
        return product;
    }
}
