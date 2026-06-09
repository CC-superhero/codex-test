package com.flashsale.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("t_invite_relation")
public class InviteRelation {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long parentId;
    private Long childId;
    private Integer level;
    private BigDecimal commissionRate;
    private LocalDateTime createTime;
}
