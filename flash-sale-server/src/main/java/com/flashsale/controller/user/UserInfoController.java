package com.flashsale.controller.user;

import com.flashsale.common.BizException;
import com.flashsale.common.Result;
import com.flashsale.entity.User;
import com.flashsale.service.UserService;
import com.flashsale.util.SecurityUtil;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/api/user")
public class UserInfoController {

    @Resource
    private UserService userService;

    @GetMapping("/info")
    public Result<User> getInfo() {
        Long userId = SecurityUtil.getCurrentUserId();
        User user = userService.getById(userId);
        if (user == null) {
            throw new BizException("用户不存在");
        }
        return Result.ok(user);
    }

    @PutMapping("/info")
    public Result<?> updateInfo(@RequestBody User user) {
        Long userId = SecurityUtil.getCurrentUserId();
        user.setId(userId);
        user.setInviteCode(null);   // 不可修改
        user.setParentId(null);
        user.setParentInviteCode(null);
        user.setPoints(null);
        user.setStatus(null);
        userService.updateById(user);
        return Result.ok();
    }
}
