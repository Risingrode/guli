package com.atguigu.gulimall.product.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

/**
 * @author 烟雨蒙蒙
 */
@Configuration
public class MyRedissonConfig {
    // 告诉 Spring 在容器关闭时调用 shutdown 方法来销毁这个 Bean
    @Bean(destroyMethod="shutdown")
    public RedissonClient redisson(@Value("${spring.redis.host}") String url) throws IOException {
        Config config = new Config();
        config.useSingleServer().setAddress("redis://"+url+":6379");
        //2、根据Config创建出RedissonClient示例  创建单例模式的配置 需要集群修改这里
        RedissonClient redissonClient = Redisson.create(config);
        return redissonClient;
    }
}

