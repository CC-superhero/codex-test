package com.flashsale.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("t_user")
public class User {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String nickname;
    private String inviteCode;
    private String parentInviteCode;
    private Long parentId;
    private String avatar;
    private String address;
    private String realName;
    private String phone;
    private String bankCard;
    private String wechatQr;
    private String alipayQr;
    private String idCard;
    private BigDecimal points;
    private Integer status;       // 0正常 1封禁
    private LocalDateTime createTime;
}
