package com.atguigu.seckill.controller;

import com.atguigu.seckill.SecKill_redis;
import com.atguigu.seckill.SecKill_redisByScript;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Random;

@RestController
public class SeckillController {

    @GetMapping("/seckill/test")
    public String test(String prodId) {
        String userId = new Random().nextInt(50000) + "";
//        boolean isSuccess = SecKill_redis.doSecKill(userId, prodId);
        boolean isSuccess = SecKill_redisByScript.doSecKill(userId, prodId);
        return String.valueOf(isSuccess);

    }


}
