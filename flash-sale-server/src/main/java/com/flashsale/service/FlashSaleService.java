package com.flashsale.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.flashsale.common.BizException;
import com.flashsale.config.RabbitMQConfig;
import com.flashsale.entity.Coupon;
import com.flashsale.entity.FlashRecord;
import com.flashsale.entity.InviteRelation;
import com.flashsale.entity.Order;
import com.flashsale.entity.PointsLog;
import com.flashsale.entity.Product;
import com.flashsale.entity.User;
import com.flashsale.entity.UserInventory;
import com.flashsale.mapper.CouponMapper;
import com.flashsale.mapper.FlashRecordMapper;
import com.flashsale.mapper.InviteRelationMapper;
import com.flashsale.mapper.OrderMapper;
import com.flashsale.mapper.PointsLogMapper;
import com.flashsale.mapper.ProductMapper;
import com.flashsale.mapper.UserInventoryMapper;
import com.flashsale.mapper.UserMapper;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

@Service
public class FlashSaleService {

    private static final String STOCK_KEY_PREFIX = "flash:stock:";

    @Resource
    private RedisTemplate<String, Object> redisTemplate;
    @Resource
    private DefaultRedisScript<Long> stockDeductScript;
    @Resource
    private RabbitTemplate rabbitTemplate;
    @Resource
    private ProductMapper productMapper;
    @Resource
    private FlashRecordMapper flashRecordMapper;
    @Resource
    private OrderMapper orderMapper;
    @Resource
    private UserInventoryMapper userInventoryMapper;
    @Resource
    private PointsLogMapper pointsLogMapper;
    @Resource
    private UserMapper userMapper;
    @Resource
    private InviteRelationMapper inviteRelationMapper;
    @Resource
    private CouponMapper couponMapper;
    @Resource
    private ConfigService configService;
    @Resource
    private ProductLifecycleService productLifecycleService;

    public Long buy(Long userId, Long productId, Long couponId, BigDecimal pointsUsed) {
        Product product = productMapper.selectById(productId);
        if (product == null) {
            throw new BizException("商品不存在");
        }

        productLifecycleService.syncProductStatus(product);
        validateProductCanBuy(product);
        validateDuplicateOrder(userId, productId);

        BigDecimal safePointsUsed = pointsUsed == null ? BigDecimal.ZERO : pointsUsed;
        if (safePointsUsed.compareTo(BigDecimal.ZERO) < 0) {
            throw new BizException("使用积分不能小于 0");
        }

        Coupon coupon = validateCoupon(userId, couponId);
        User buyer = validatePointsBalance(userId, safePointsUsed);

        String lockKey = "flash:lock:" + productId + ":" + userId;
        Boolean locked = redisTemplate.opsForValue().setIfAbsent(lockKey, "1", 10, TimeUnit.SECONDS);
        if (Boolean.FALSE.equals(locked)) {
            throw new BizException("请勿重复抢购");
        }

        String stockKey = STOCK_KEY_PREFIX + productId;
        if (!Boolean.TRUE.equals(redisTemplate.hasKey(stockKey))) {
            warmupStock(productId);
        }

        Long remain = redisTemplate.execute(stockDeductScript, Collections.singletonList(stockKey));
        if (remain == null || remain < 0) {
            throw new BizException("商品已抢光");
        }

        BigDecimal couponAmount = coupon != null ? coupon.getFaceValue() : BigDecimal.ZERO;
        BigDecimal amount = product.getPrice().subtract(couponAmount).subtract(safePointsUsed);
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            amount = BigDecimal.ZERO;
        }

        Order order = new Order();
        order.setUserId(userId);
        order.setProductId(productId);
        order.setAmount(amount);
        order.setPointsUsed(safePointsUsed);
        order.setCouponId(couponId);
        order.setStatus(0);

        boolean couponReserved = false;
        boolean pointsReserved = false;

        try {
            if (coupon != null) {
                coupon.setStatus(1);
                couponMapper.updateById(coupon);
                couponReserved = true;
            }

            if (safePointsUsed.compareTo(BigDecimal.ZERO) > 0) {
                buyer.setPoints(buyer.getPoints().subtract(safePointsUsed));
                userMapper.updateById(buyer);
                pointsReserved = true;
            }

            orderMapper.insert(order);

            FlashOrderMessage message = new FlashOrderMessage();
            message.setOrderId(order.getId());
            message.setUserId(userId);
            message.setProductId(productId);
            message.setCouponId(couponId);
            message.setPointsUsed(safePointsUsed);
            rabbitTemplate.convertAndSend(RabbitMQConfig.FLASH_ORDER_QUEUE, message);
            return order.getId();
        } catch (Exception ex) {
            if (order.getId() != null) {
                orderMapper.deleteById(order.getId());
            }
            if (couponReserved && coupon != null) {
                coupon.setStatus(0);
                couponMapper.updateById(coupon);
            }
            if (pointsReserved) {
                User latestBuyer = userMapper.selectById(userId);
                latestBuyer.setPoints(latestBuyer.getPoints().add(safePointsUsed));
                userMapper.updateById(latestBuyer);
            }
            redisTemplate.opsForValue().increment(stockKey);
            throw new BizException("抢购提交失败，请稍后重试");
        }
    }

    public void warmupStock(Long productId) {
        Product product = productMapper.selectById(productId);
        if (product != null) {
            redisTemplate.opsForValue().set(STOCK_KEY_PREFIX + productId, product.getRemainStock());
        }
    }

    @Transactional
    public void processFlashOrder(FlashOrderMessage message) {
        Order order = orderMapper.selectById(message.getOrderId());
        if (order == null || Integer.valueOf(1).equals(order.getStatus())) {
            return;
        }

        Product product = productMapper.selectById(message.getProductId());
        if (product == null) {
            order.setStatus(2);
            orderMapper.updateById(order);
            throw new BizException("商品不存在");
        }

        int updated = productMapper.update(null, new LambdaUpdateWrapper<Product>()
                .eq(Product::getId, message.getProductId())
                .gt(Product::getRemainStock, 0)
                .setSql("remain_stock = remain_stock - 1"));
        if (updated == 0) {
            order.setStatus(2);
            orderMapper.updateById(order);
            throw new BizException("数据库库存不足");
        }

        Product latestProduct = productMapper.selectById(message.getProductId());
        latestProduct.setStatus(productLifecycleService.calculateStatus(latestProduct, LocalDateTime.now()));
        productMapper.updateById(latestProduct);

        if (order.getPointsUsed() != null && order.getPointsUsed().compareTo(BigDecimal.ZERO) > 0) {
            PointsLog consumeLog = new PointsLog();
            consumeLog.setUserId(order.getUserId());
            consumeLog.setChangeAmount(order.getPointsUsed().negate());
            consumeLog.setType(4);
            consumeLog.setRemark("秒杀使用积分抵扣");
            pointsLogMapper.insert(consumeLog);
        }

        FlashRecord record = new FlashRecord();
        record.setUserId(order.getUserId());
        record.setProductId(order.getProductId());
        record.setAmount(order.getAmount());
        flashRecordMapper.insert(record);

        UserInventory inventory = new UserInventory();
        inventory.setUserId(order.getUserId());
        inventory.setProductId(order.getProductId());
        inventory.setProductName(latestProduct.getName());
        inventory.setProductImage(latestProduct.getImage());
        inventory.setPrice(order.getAmount());
        inventory.setSource(1);
        inventory.setStatus(1);
        userInventoryMapper.insert(inventory);

        BigDecimal pointsRate = configService.getPointsRate();
        BigDecimal earnPoints = order.getAmount().multiply(pointsRate);
        if (earnPoints.compareTo(BigDecimal.ZERO) > 0) {
            User buyer = userMapper.selectById(order.getUserId());
            buyer.setPoints(buyer.getPoints().add(earnPoints));
            userMapper.updateById(buyer);

            PointsLog rewardLog = new PointsLog();
            rewardLog.setUserId(order.getUserId());
            rewardLog.setChangeAmount(earnPoints);
            rewardLog.setType(1);
            rewardLog.setRemark("秒杀奖励积分");
            pointsLogMapper.insert(rewardLog);
        }

        distributeInviteCommission(order.getUserId(), order.getAmount());

        order.setStatus(1);
        orderMapper.updateById(order);
    }

    private void validateProductCanBuy(Product product) {
        LocalDateTime now = LocalDateTime.now();
        if (product.getStartTime() != null && now.isBefore(product.getStartTime())) {
            throw new BizException("秒杀尚未开始");
        }
        if (product.getEndTime() != null && !now.isBefore(product.getEndTime())) {
            product.setStatus(2);
            productMapper.updateById(product);
            throw new BizException("秒杀已结束");
        }
        if (product.getRemainStock() == null || product.getRemainStock() <= 0) {
            product.setStatus(2);
            productMapper.updateById(product);
            throw new BizException("商品已抢光");
        }
        if (!Integer.valueOf(1).equals(product.getStatus())) {
            throw new BizException("商品当前不可抢购");
        }
    }

    private void validateDuplicateOrder(Long userId, Long productId) {
        Long count = orderMapper.selectCount(new LambdaQueryWrapper<Order>()
                .eq(Order::getUserId, userId)
                .eq(Order::getProductId, productId)
                .in(Order::getStatus, 0, 1));
        if (count != null && count > 0) {
            throw new BizException("同一商品只能抢购一次");
        }
    }

    private Coupon validateCoupon(Long userId, Long couponId) {
        if (couponId == null) {
            return null;
        }

        Coupon coupon = couponMapper.selectById(couponId);
        if (coupon == null || !userId.equals(coupon.getUserId())) {
            throw new BizException("优惠券不存在");
        }
        if (!Integer.valueOf(0).equals(coupon.getStatus())) {
            throw new BizException("优惠券已使用或已过期");
        }
        if (coupon.getExpireTime() != null && !coupon.getExpireTime().isAfter(LocalDateTime.now())) {
            coupon.setStatus(2);
            couponMapper.updateById(coupon);
            throw new BizException("优惠券已过期");
        }
        return coupon;
    }

    private User validatePointsBalance(Long userId, BigDecimal pointsUsed) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BizException("用户不存在");
        }
        BigDecimal currentPoints = user.getPoints() == null ? BigDecimal.ZERO : user.getPoints();
        user.setPoints(currentPoints);
        if (pointsUsed.compareTo(currentPoints) > 0) {
            throw new BizException("积分余额不足");
        }
        return user;
    }

    private void distributeInviteCommission(Long userId, BigDecimal amount) {
        InviteRelation relation = inviteRelationMapper.selectOne(new LambdaQueryWrapper<InviteRelation>()
                .eq(InviteRelation::getChildId, userId));
        if (relation == null) {
            return;
        }

        BigDecimal commission = amount.multiply(relation.getCommissionRate());
        if (commission.compareTo(BigDecimal.ZERO) <= 0) {
            return;
        }

        User parent = userMapper.selectById(relation.getParentId());
        if (parent == null || Integer.valueOf(1).equals(parent.getStatus())) {
            return;
        }

        parent.setPoints(parent.getPoints().add(commission));
        userMapper.updateById(parent);

        PointsLog log = new PointsLog();
        log.setUserId(parent.getId());
        log.setChangeAmount(commission);
        log.setType(2);
        log.setRemark("下级用户 " + userId + " 秒杀奖励");
        pointsLogMapper.insert(log);
    }

    public static class FlashOrderMessage {
        private Long orderId;
        private Long userId;
        private Long productId;
        private Long couponId;
        private BigDecimal pointsUsed;

        public Long getOrderId() {
            return orderId;
        }

        public void setOrderId(Long orderId) {
            this.orderId = orderId;
        }

        public Long getUserId() {
            return userId;
        }

        public void setUserId(Long userId) {
            this.userId = userId;
        }

        public Long getProductId() {
            return productId;
        }

        public void setProductId(Long productId) {
            this.productId = productId;
        }

        public Long getCouponId() {
            return couponId;
        }

        public void setCouponId(Long couponId) {
            this.couponId = couponId;
        }

        public BigDecimal getPointsUsed() {
            return pointsUsed;
        }

        public void setPointsUsed(BigDecimal pointsUsed) {
            this.pointsUsed = pointsUsed;
        }
    }
}
