package com.flashsale.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("t_points_log")
public class PointsLog {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private BigDecimal changeAmount;
    private Integer type;         // 1抢购转化 2邀请奖励 3手续费返还 4后台发放
    private String remark;
    private LocalDateTime createTime;
}
