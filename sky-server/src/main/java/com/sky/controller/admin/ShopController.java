package com.sky.controller.admin;

import com.sky.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;



@Slf4j
@RestController("adminShopController")
@RequestMapping("/admin/shop")
@Api(tags = "店铺")
public class ShopController {
    private static final String KEY = "SHOP_STATUS";
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @GetMapping("/status")
    @ApiOperation("获取店铺状态")
    public Result<Integer> getStatus() {

        Integer status = (Integer) redisTemplate.opsForValue().get(KEY);
        log.info("获取店铺状态:{}", status ==1 ? "营业中":"打烊中");
        return Result.success(status);
    }

    @PutMapping("/{status}")
    @ApiOperation("设置店铺状态")
    public Result updateStatus(@PathVariable("status") Integer status) {
    log.info("设置店铺的状态{}", status == 1 ? "营业中":"打烊中");
    redisTemplate.opsForValue().set(KEY, status);

        return Result.success();
    }
}
