package com.flashsale.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.flashsale.common.BizException;
import com.flashsale.common.Result;
import com.flashsale.entity.InviteRelation;
import com.flashsale.entity.User;
import com.flashsale.entity.UserInventory;
import com.flashsale.mapper.InviteRelationMapper;
import com.flashsale.service.InventoryService;
import com.flashsale.service.UserService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminUserController {

    @Resource
    private UserService userService;
    @Resource
    private InviteRelationMapper inviteRelationMapper;
    @Resource
    private InventoryService inventoryService;

    /** 用户列表（分页） */
    @GetMapping("/user/list")
    public Result<Page<User>> userList(@RequestParam(defaultValue = "1") Integer page,
                                        @RequestParam(defaultValue = "20") Integer size,
                                        @RequestParam(required = false) String keyword) {
        Page<User> p = new Page<>(page, size);
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.like(User::getNickname, keyword);
        }
        wrapper.orderByDesc(User::getCreateTime);
        return Result.ok(userService.page(p, wrapper));
    }

    /** 修改用户信息 */
    @PutMapping("/user/{id}")
    public Result<?> updateUser(@PathVariable Long id, @RequestBody User user) {
        user.setId(id);
        user.setInviteCode(null);
        user.setParentId(null);
        user.setParentInviteCode(null);
        userService.updateById(user);
        return Result.ok();
    }

    /** 封禁/解封 */
    @PutMapping("/user/{id}/ban")
    public Result<?> banUser(@PathVariable Long id, @RequestParam Integer status) {
        User user = userService.getById(id);
        if (user == null) throw new BizException("用户不存在");
        user.setStatus(status);
        userService.updateById(user);
        return Result.ok(status == 1 ? "已封禁" : "已解封");
    }

    /** 删除用户 */
    @DeleteMapping("/user/{id}")
    public Result<?> deleteUser(@PathVariable Long id) {
        userService.removeById(id);
        return Result.ok();
    }

    /** 查看用户邀请关系树 */
    @GetMapping("/user/{id}/invite-tree")
    public Result<Map<String, Object>> inviteTree(@PathVariable Long id) {
        User user = userService.getById(id);
        if (user == null) throw new BizException("用户不存在");

        // 上级
        User parent = null;
        if (user.getParentId() != null) {
            parent = userService.getById(user.getParentId());
        }

        // 下级列表
        List<User> children = userService.list(
                new LambdaQueryWrapper<User>().eq(User::getParentId, id));

        // 级联下级
        List<Map<String, Object>> fullTree = new ArrayList<>();
        for (User child : children) {
            List<User> grandChildren = userService.list(
                    new LambdaQueryWrapper<User>().eq(User::getParentId, child.getId()));
            Map<String, Object> node = new HashMap<>();
            node.put("user", child);
            node.put("children", grandChildren);
            fullTree.add(node);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("user", user);
        result.put("parent", parent);
        result.put("tree", fullTree);
        return Result.ok(result);
    }

    /** 查看用户库存 */
    @GetMapping("/user/{id}/inventory")
    public Result<List<UserInventory>> userInventory(@PathVariable Long id) {
        List<UserInventory> list = inventoryService.list(
                new LambdaQueryWrapper<UserInventory>()
                        .eq(UserInventory::getUserId, id)
                        .orderByDesc(UserInventory::getCreateTime));
        return Result.ok(list);
    }
}
