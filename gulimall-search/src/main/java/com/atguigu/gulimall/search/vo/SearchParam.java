package com.atguigu.gulimall.search.vo;

import lombok.Data;

import java.util.List;

// 封装页面所有可能传递过来的查询条件
// 例子：catalog3Id=225&keyword=小米&sort=saleCount_desc&hasStock=1&skuPrice=1_5000&brandId=1&brandId=2&attrs=1_2寸:4.5寸&attrs=2_16G:8G
@Data
public class SearchParam {
    private String keyword; // 页面传递过来的全文匹配关键字
    private Long catalog3Id; // 三级分类id
    // 经过筛选条件
    private String sort; // 排序条件
    /**
    * 好多的过滤条件
     * hasStock=0/1 是否有货
     * skuPrice=1_500/_500/500_/
     * brandId=1&brandId=2
     * attrs=1_2寸:4.5寸&attrs=2_16G:8G
     * 以上的条件都可能有，也可能没有
    */
    private Integer hasStock; // 是否有货
    private String skuPrice; // 价格区间
    private List<Long> brandId; // 品牌id
    private List<String> attrs; // 按照属性进行筛选
    private Integer pageNum; // 页码



}
