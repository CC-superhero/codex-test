package com.flashsale.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.flashsale.entity.Config;
import com.flashsale.mapper.ConfigMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class ConfigService extends ServiceImpl<ConfigMapper, Config> {

    private String getConfigValue(String key) {
        Config config = getOne(new LambdaQueryWrapper<Config>().eq(Config::getConfigKey, key));
        return config != null ? config.getConfigValue() : null;
    }

    public BigDecimal getPointsRate() {
        String val = getConfigValue("flash.points.rate");
        return val != null ? new BigDecimal(val) : BigDecimal.ONE;
    }

    public BigDecimal getInviteCommissionRate() {
        String val = getConfigValue("invite.commission.rate");
        return val != null ? new BigDecimal(val) : new BigDecimal("0.05");
    }

    public BigDecimal getListingFeeRate() {
        String val = getConfigValue("listing.fee.rate");
        return val != null ? new BigDecimal(val) : new BigDecimal("0.02");
    }
}
