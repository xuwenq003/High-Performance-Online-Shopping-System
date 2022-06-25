package com.jiuzhang.seckill;

import com.jiuzhang.seckill.service.SeckillActivityService;
import com.jiuzhang.seckill.util.RedisService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
public class RedisDemoTest {
    @Resource
    private RedisService redisService;

    @Test
    public void stockTest() {
        String value = redisService.setValue("stock:19", 10L).getValue("stock:19");
        System.out.println(value);
    }

    @Autowired
    SeckillActivityService seckillActivityService;

    @Test
    public void pushSeckillInfoRedisTest() {
        seckillActivityService.pushSeckillInfoToRedis(19);
    }
}
