package com.flashsale.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.flashsale.common.BizException;
import com.flashsale.common.JwtUtil;
import com.flashsale.dto.AdminLoginDTO;
import com.flashsale.entity.Admin;
import com.flashsale.mapper.AdminMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@Service
public class AdminService extends ServiceImpl<AdminMapper, Admin> {

    @Resource
    private JwtUtil jwtUtil;
    @Resource
    private PasswordEncoder passwordEncoder;

    public Map<String, Object> login(AdminLoginDTO dto) {
        Admin admin = getOne(new LambdaQueryWrapper<Admin>()
                .eq(Admin::getUsername, dto.getUsername()));
        if (admin == null || !passwordEncoder.matches(dto.getPassword(), admin.getPassword())) {
            throw new BizException("用户名或密码错误");
        }

        String token = jwtUtil.generateToken(admin.getId(), admin.getUsername(), true);

        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("username", admin.getUsername());
        return result;
    }
}
