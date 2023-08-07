package com.atguigu.gulimall.product;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
* 整合mybatis-plus
 * 使用逻辑删除
 * 1.配置全局的逻辑删除规则（省略）
 * 2.配置逻辑删除的组件Bean（省略）
 * 3.给Bean加上逻辑删除注解@TableLogic
 * 4.在yml中配置逻辑删除的属性
 * 5.测试逻辑删除
 * 6.逻辑删除的crud
 * 7.查询的默认是查询未删除的数据
 * 8.查询已删除的数据
 *     直接在sql语句中拼接where deleted=1
 *     1.配置全局的逻辑删除规则（省略）
 *     2.配置逻辑删除的组件Bean（省略）
 *     3.给Bean加上逻辑删除注解@TableLogic
 *     4.在yml中配置逻辑删除的属性
 *     5.测试逻辑删除
 *     6.逻辑删除的crud
*/

/**
    1. JSR303数据校验
          1)、给Bean添加校验注解:javax.validation.constraints，并定义自己的message提示
          2)、开启校验功能 @Valid
          3)、校验失败会有默认的响应
          4)、给校验的bean后紧跟一个BindingResult，就可以获取到校验的结果
    2. 统一的异常处理
             1)、编写异常处理类，使用@ControllerAdvice
            2)、使用@ExceptionHandler标注方法可以处理的异常
    3. 自定义异常
               1)、自定义异常类型
                2)、使用@ResponseStatus(value=HttpStatus.状态码，reason="异常信息")，标注在自定义异常类上
                3)、自定义异常处理器，@ExceptionHandler(自定义异常类.class)
    4. 自定义校验器
                  1)、编写一个自定义的校验器
                 2)、编写一个自定义的校验器注解
                 3)、关联自定义的校验器和自定义的校验器注解
                 4)、使用自定义的校验器注解
*/

// 扫描feign接口所在的包
@EnableFeignClients(basePackages = "com.atguigu.gulimall.product.feign")
@MapperScan("com.atguigu.gulimall.product.dao")
@SpringBootApplication
@EnableDiscoveryClient
public class GulimallProductApplication {

    public static void main(String[] args) {
        SpringApplication.run(GulimallProductApplication.class, args);
    }

}
