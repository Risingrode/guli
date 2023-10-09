package com.atguigu.gulimall.product.web;

import com.atguigu.gulimall.product.entity.CategoryEntity;
import com.atguigu.gulimall.product.service.CategoryService;

import com.atguigu.gulimall.product.vo.Catelog2Vo;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

// 开发页面跳转功能
@Controller
public class IndexController {

    @Autowired
    CategoryService categoryService;

    @Autowired
    RedissonClient redisson;

    @GetMapping({"/", "/index.html"})
    public String indexPage(Model model) {// Model模型,可以用来存数据,可以在页面取出来

        // TODO 1. 查出所有的一级分类
        List<CategoryEntity> categoryEntities = categoryService.getLevel1Categories();

        // 视图解析器进行拼串
        // classpath 代表类路径,就是resources
        // classpath:/templates/xxxx.html
        model.addAttribute("categories", categoryEntities);

        return "index";
    }

    // index/catalog.json
    // 1)、springboot静态资源怎么访问； /hello.html；去静态资源文件夹找hello.html
    @ResponseBody
    @GetMapping("/index/catalog.json")
    public Map<String, List<Catelog2Vo>> getCatelogJson() {
        return categoryService.getCatalogJson();
    }

    @ResponseBody
    @GetMapping("/hello")
    public String hello(){
        RLock lock = redisson.getLock("my-lock");
        lock.lock();
        //问题：lock.lock(10,TimeUnit.SECONDS); 在锁时间到了以后，不会自动续期。
        //    如果我们未指定锁的超时时间，就使用30 * 1000【LockWatchdogTimeout看门狗的默认时间】;
        //    只要占锁成功，就会启动一个定时任务【重新给锁设置过期时间，新的过期时间就是看门狗的默认时间】,每隔10s都会自动再次续期，续成30s
        //1）、lock.lock(30,TimeUnit.SECONDS);省掉了整个续期操作。手动解锁
        try{
            System.out.println("加锁成功，执行业务..."+Thread.currentThread().getId());
            Thread.sleep(30000);
        }catch (Exception e){

        }finally {
            //3、解锁  将设解锁代码没有运行，redisson会不会出现死锁
            System.out.println("释放锁..."+Thread.currentThread().getId());
            lock.unlock();
        }

        return "hello";
    }

}
