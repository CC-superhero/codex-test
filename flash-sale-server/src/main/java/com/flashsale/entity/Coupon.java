package com.flashsale.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("t_coupon")
public class Coupon {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private String name;
    private BigDecimal faceValue;
    private LocalDateTime expireTime;
    private Integer status;       // 0未使用 1已使用 2已过期
    private LocalDateTime createTime;
}
