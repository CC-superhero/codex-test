package com.flashsale.controller.admin;

import com.flashsale.common.Result;
import com.flashsale.dto.AdminLoginDTO;
import com.flashsale.service.AdminService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminAuthController {

    @Resource
    private AdminService adminService;

    @PostMapping("/login")
    public Result<Map<String, Object>> login(@Valid @RequestBody AdminLoginDTO dto) {
        return Result.ok(adminService.login(dto));
    }
}
