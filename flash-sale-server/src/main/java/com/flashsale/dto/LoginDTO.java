package com.flashsale.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class LoginDTO {
    @NotBlank(message = "昵称不能为空")
    private String nickname;
}
