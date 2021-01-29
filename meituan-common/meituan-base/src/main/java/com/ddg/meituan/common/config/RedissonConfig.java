package com.ddg.meituan.common.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

/**
 * @Author: DDG
 * @Date: 2020/5/20 10:23
 * @Description:
 */
@Configuration
public class RedissonConfig {

    /**
     * 所有的redis操作都是需要通过redisclient对象
     * @return
     * @throws IOException
     */
    @Bean(destroyMethod="shutdown")
    public RedissonClient redissonClient() throws IOException {
        Config config = new Config();
        // 目前使用的是单节点模式：需要注意的就是redis地址有问题 redis配置的address 需要使用redis://
        // 或者是rediss://开头 如果redis启动的是ssl安全链接 使用rediss://
        config.useSingleServer()
                .setAddress("redis://172.20.10.3:6379");
        return Redisson.create(config);
    }

}
