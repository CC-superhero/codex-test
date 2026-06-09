package com.flashsale.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("t_product")
public class Product {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private BigDecimal price;
    private String image;
    private Integer totalStock;
    private Integer remainStock;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer status;       // 0未开始 1进行中 2已结束
    private LocalDateTime createTime;
}
