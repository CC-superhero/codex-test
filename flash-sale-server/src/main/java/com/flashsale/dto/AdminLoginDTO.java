package com.flashsale.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class AdminLoginDTO {
    @NotBlank
    private String username;
    @NotBlank
    private String password;
}
