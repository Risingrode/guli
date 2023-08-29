package com.atguigu.gulimall.product.web;

import com.atguigu.gulimall.product.entity.CategoryEntity;
import com.atguigu.gulimall.product.service.CategoryService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class IndexController {

    @Autowired
    CategoryService categoryService;

    @GetMapping({"/","/index.html"})
    public String index(Model model){

        // TODO 1. 查出所有的一级分类
        List<CategoryEntity> categoryEntities=categoryService.getLevel1Categories();

        // 视图解析器进行拼串
        // classpath:/templates/xxxx.html
        model.addAttribute("categories",categoryEntities);

        return "index";
    }


}
























