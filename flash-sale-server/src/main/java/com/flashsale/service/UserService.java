package com.flashsale.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.flashsale.common.BizException;
import com.flashsale.common.JwtUtil;
import com.flashsale.dto.LoginDTO;
import com.flashsale.dto.RegisterDTO;
import com.flashsale.entity.InviteRelation;
import com.flashsale.entity.User;
import com.flashsale.mapper.InviteRelationMapper;
import com.flashsale.mapper.UserMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class UserService extends ServiceImpl<UserMapper, User> {

    @Resource
    private JwtUtil jwtUtil;
    @Resource
    private InviteRelationMapper inviteRelationMapper;
    @Resource
    private ConfigService configService;

    @Transactional
    public Map<String, Object> register(RegisterDTO dto) {
        // 校验邀请码是否存在
        User parent = getOne(new LambdaQueryWrapper<User>()
                .eq(User::getInviteCode, dto.getInviteCode()));
        if (parent == null) {
            throw new BizException("邀请码无效");
        }
        if (parent.getStatus() == 1) {
            throw new BizException("邀请用户已被封禁");
        }

        // 校验昵称唯一
        long count = count(new LambdaQueryWrapper<User>()
                .eq(User::getNickname, dto.getNickname()));
        if (count > 0) {
            throw new BizException("昵称已被使用");
        }

        // 创建用户
        User user = new User();
        user.setNickname(dto.getNickname());
        user.setInviteCode(generateInviteCode());
        user.setParentInviteCode(dto.getInviteCode());
        user.setParentId(parent.getId());
        user.setAvatar(dto.getAvatar());
        user.setAddress(dto.getAddress());
        user.setRealName(dto.getRealName());
        user.setPhone(dto.getPhone());
        user.setBankCard(dto.getBankCard());
        user.setWechatQr(dto.getWechatQr());
        user.setAlipayQr(dto.getAlipayQr());
        user.setIdCard(dto.getIdCard());
        save(user);

        // 建立邀请关系
        InviteRelation relation = new InviteRelation();
        relation.setParentId(parent.getId());
        relation.setChildId(user.getId());
        relation.setLevel(1);
        relation.setCommissionRate(configService.getInviteCommissionRate());
        inviteRelationMapper.insert(relation);

        // 生成 token
        String token = jwtUtil.generateToken(user.getId(), user.getNickname(), false);

        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("userId", user.getId());
        result.put("nickname", user.getNickname());
        result.put("inviteCode", user.getInviteCode());
        return result;
    }

    public Map<String, Object> login(LoginDTO dto) {
        User user = getOne(new LambdaQueryWrapper<User>()
                .eq(User::getNickname, dto.getNickname()));
        if (user == null) {
            throw new BizException("用户不存在");
        }
        if (user.getStatus() == 1) {
            throw new BizException("账号已被封禁");
        }

        String token = jwtUtil.generateToken(user.getId(), user.getNickname(), false);

        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("userId", user.getId());
        result.put("nickname", user.getNickname());
        return result;
    }

    private String generateInviteCode() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();
    }
}
