package com.flashsale.controller.user;

import com.flashsale.common.Result;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class PayController {

    /** 模拟支付页面 - 不做真实支付，返回模拟结果 */
    @PostMapping("/pay/page")
    public Result<Map<String, Object>> payPage() {
        Map<String, Object> result = new HashMap<>();
        result.put("status", "paid");
        result.put("message", "支付成功（模拟）");
        result.put("payTime", System.currentTimeMillis());
        return Result.ok(result);
    }
}
