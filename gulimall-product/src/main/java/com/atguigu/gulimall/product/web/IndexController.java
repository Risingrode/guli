package com.atguigu.gulimall.product.web;

import com.atguigu.gulimall.product.entity.CategoryEntity;
import com.atguigu.gulimall.product.service.CategoryService;

import com.atguigu.gulimall.product.vo.Catelog2Vo;
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

    @GetMapping({"/","/index.html"})
    public String indexPage(Model model){// Model模型,可以用来存数据,可以在页面取出来

        // TODO 1. 查出所有的一级分类
        List<CategoryEntity> categoryEntities=categoryService.getLevel1Categories();

        // 视图解析器进行拼串
        // classpath 代表类路径,就是resources
        // classpath:/templates/xxxx.html
        model.addAttribute("categories",categoryEntities);

        return "index";
    }
    // index/catalog.json
    // 1)、springboot静态资源怎么访问； /hello.html；去静态资源文件夹找hello.html
    @ResponseBody
    @GetMapping("/index/catalog.json")
    public Map<String, List<Catelog2Vo>> getCatelogJson(){

        return categoryService.getCatalogJson();

    }

}
