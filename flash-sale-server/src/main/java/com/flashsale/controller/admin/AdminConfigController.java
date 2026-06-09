package com.flashsale.controller.admin;

import com.flashsale.common.Result;
import com.flashsale.entity.Config;
import com.flashsale.mapper.ConfigMapper;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminConfigController {

    @Resource
    private ConfigMapper configMapper;

    @GetMapping("/config")
    public Result<List<Config>> getConfig() {
        return Result.ok(configMapper.selectList(null));
    }

    @PutMapping("/config")
    public Result<?> updateConfig(@RequestBody Config config) {
        configMapper.updateById(config);
        return Result.ok();
    }
}
