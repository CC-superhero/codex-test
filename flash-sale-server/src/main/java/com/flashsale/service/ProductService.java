package com.flashsale.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.flashsale.entity.Product;
import com.flashsale.mapper.ProductMapper;
import org.springframework.stereotype.Service;

@Service
public class ProductService extends ServiceImpl<ProductMapper, Product> {
}
