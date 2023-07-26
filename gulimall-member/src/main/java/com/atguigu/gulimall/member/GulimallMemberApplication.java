package com.atguigu.gulimall.member;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

// 想要远程调用别的服务
// 1. 引入open-feign
// 2. 编写一个接口，告诉springcloud这个接口需要调用远程服务
// 3. 开启远程调用功能
@EnableFeignClients(basePackages = "com.atguigu.gulimall.member.feign")
@SpringBootApplication
@EnableDiscoveryClient
public class GulimallMemberApplication {

    public static void main(String[] args) {
        SpringApplication.run(GulimallMemberApplication.class, args);
    }

}
