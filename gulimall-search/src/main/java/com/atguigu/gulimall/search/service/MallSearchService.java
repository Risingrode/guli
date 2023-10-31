package com.atguigu.gulimall.search.service;

import com.atguigu.gulimall.search.vo.SearchParam;

public interface MallSearchService{
    // 检索的所有参数，最终返回所有结果
    Object search(SearchParam param);

}
