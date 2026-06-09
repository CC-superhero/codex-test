package com.flashsale.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class RegisterDTO {
    @NotBlank(message = "昵称不能为空")
    private String nickname;

    @NotBlank(message = "邀请码不能为空")
    private String inviteCode;

    private String avatar;
    private String address;
    private String realName;
    private String phone;
    private String bankCard;
    private String wechatQr;
    private String alipayQr;
    private String idCard;
}
