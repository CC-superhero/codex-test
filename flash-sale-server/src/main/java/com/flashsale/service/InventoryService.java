package com.flashsale.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.flashsale.common.BizException;
import com.flashsale.entity.PointsLog;
import com.flashsale.entity.User;
import com.flashsale.entity.UserInventory;
import com.flashsale.mapper.PointsLogMapper;
import com.flashsale.mapper.UserInventoryMapper;
import com.flashsale.mapper.UserMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;

@Service
public class InventoryService extends ServiceImpl<UserInventoryMapper, UserInventory> {

    @Resource
    private PointsLogMapper pointsLogMapper;
    @Resource
    private UserMapper userMapper;
    @Resource
    private ConfigService configService;

    @Transactional
    public void listProduct(Long userId, Long inventoryId) {
        UserInventory item = getById(inventoryId);
        if (item == null || !item.getUserId().equals(userId)) {
            throw new BizException("库存不存在");
        }
        if (Integer.valueOf(2).equals(item.getStatus())) {
            throw new BizException("商品已上架");
        }

        item.setStatus(2);
        updateById(item);

        BigDecimal feeRate = configService.getListingFeeRate();
        BigDecimal fee = item.getPrice().multiply(feeRate);
        if (fee.compareTo(BigDecimal.ZERO) > 0) {
            User user = userMapper.selectById(userId);
            user.setPoints(user.getPoints().add(fee));
            userMapper.updateById(user);

            PointsLog log = new PointsLog();
            log.setUserId(userId);
            log.setChangeAmount(fee);
            log.setType(3);
            log.setRemark("上架返还手续费积分");
            pointsLogMapper.insert(log);
        }
    }

    public void delistProduct(Long userId, Long inventoryId) {
        UserInventory item = getById(inventoryId);
        if (item == null || !item.getUserId().equals(userId)) {
            throw new BizException("库存不存在");
        }
        if (Integer.valueOf(1).equals(item.getStatus())) {
            throw new BizException("商品未上架");
        }
        item.setStatus(1);
        updateById(item);
    }

    @Transactional
    public void transferInventory(Long inventoryId, Long targetUserId) {
        UserInventory item = getById(inventoryId);
        if (item == null) {
            throw new BizException("库存不存在");
        }
        User targetUser = userMapper.selectById(targetUserId);
        if (targetUser == null) {
            throw new BizException("目标用户不存在");
        }
        item.setUserId(targetUserId);
        item.setStatus(1);
        updateById(item);
    }

    public void deleteInventory(Long inventoryId) {
        removeById(inventoryId);
    }

    public List<UserInventory> marketInventory(Long currentUserId) {
        return list(new LambdaQueryWrapper<UserInventory>()
                .eq(UserInventory::getStatus, 2)
                .ne(currentUserId != null, UserInventory::getUserId, currentUserId)
                .orderByDesc(UserInventory::getCreateTime));
    }

    @Transactional
    public void buyListedProduct(Long buyerId, Long inventoryId) {
        UserInventory item = getById(inventoryId);
        if (item == null) {
            throw new BizException("商品不存在");
        }
        if (!Integer.valueOf(2).equals(item.getStatus())) {
            throw new BizException("商品未上架");
        }
        if (buyerId.equals(item.getUserId())) {
            throw new BizException("不能购买自己上架的商品");
        }

        User buyer = userMapper.selectById(buyerId);
        if (buyer == null || Integer.valueOf(1).equals(buyer.getStatus())) {
            throw new BizException("当前用户不可购买");
        }

        item.setUserId(buyerId);
        item.setStatus(1);
        item.setSource(2);
        updateById(item);
    }
}
