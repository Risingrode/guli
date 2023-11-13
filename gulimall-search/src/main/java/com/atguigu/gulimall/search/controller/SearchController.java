package com.atguigu.gulimall.search.controller;

import com.atguigu.gulimall.search.service.MallSearchService;
import com.atguigu.gulimall.search.vo.SearchParam;
import com.atguigu.gulimall.search.vo.SearchResVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;

@Controller
public class SearchController {
    @Autowired
    private MallSearchService searchService;

    /**
     * @Description:
     * @Param: param : 把页面提交的参数封装成一个对象
     * @return: model : 保存查询结果对象
     */

    @GetMapping("/list.html")
    public String listPage(SearchParam param, Model model, HttpServletRequest request) {
        //Object result = mallSearchService.search(param);
        param.set_queryString(request.getQueryString());
        SearchResVo searchResVo = (SearchResVo) searchService.search(param);
        model.addAttribute("result", searchResVo);
        return "list";
    }
}
