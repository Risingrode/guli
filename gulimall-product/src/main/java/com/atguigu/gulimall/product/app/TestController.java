package com.atguigu.gulimall.product.app;

import org.redisson.api.RSemaphore;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("product/Test")
public class TestController {
     // Redisson 测试
    @Autowired
    RedissonClient redisson;

    @GetMapping("/park")
    @ResponseBody
    public String park() throws InterruptedException {
        RSemaphore park = redisson.getSemaphore("park");
        boolean b = park.tryAcquire();
        if(b){
            Thread.sleep(30000);
        }else {
            return "error";
        }
        return "ok=>"+b;
    }

    // 当前方法就是开走一辆车，清空一个车位
    @GetMapping("/go")
    @ResponseBody
    public String go() throws InterruptedException {
        RSemaphore park = redisson.getSemaphore("park");
        park.release();//释放一个车位
        return "ok";
    }
}
