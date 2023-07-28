package com.atguigu.gulimall.product;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
* 整合mybatis-plus
 * 使用逻辑删除
 * 1.配置全局的逻辑删除规则（省略）
 * 2.配置逻辑删除的组件Bean（省略）
 * 3.给Bean加上逻辑删除注解@TableLogic
 * 4.在yml中配置逻辑删除的属性
 * 5.测试逻辑删除
 * 6.逻辑删除的crud
*/

@MapperScan("com.atguigu.gulimall.product.dao")
@SpringBootApplication
@EnableDiscoveryClient
public class GulimallProductApplication {

    public static void main(String[] args) {
        SpringApplication.run(GulimallProductApplication.class, args);
    }

}
