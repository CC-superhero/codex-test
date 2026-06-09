package com.flashsale.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("t_order")
public class Order {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private Long productId;
    private BigDecimal amount;
    private BigDecimal pointsUsed;
    private Long couponId;
    private Integer status;       // 0待处理 1已完成 2已失败
    private LocalDateTime createTime;
}
