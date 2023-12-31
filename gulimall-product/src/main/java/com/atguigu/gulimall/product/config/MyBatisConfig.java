package com.atguigu.gulimall.product.config;

import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement//开启事务 方法可以使用事务了
@MapperScan("com.atguigu.gulimall.product.dao")
public class MyBatisConfig {
    // 引入分页插件
    @Bean
    public PaginationInterceptor paginationInterceptor() {
        PaginationInterceptor paginationInterceptor = new PaginationInterceptor();
        paginationInterceptor.setOverflow(true);//设置请求的页面大于最大页后操作，true调回到首页，false继续请求，默认false
        paginationInterceptor.setLimit(1000);//设置最大单页限制数量，默认500条，-1不受限制
        return new PaginationInterceptor();
    }
}






