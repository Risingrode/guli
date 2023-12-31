package com.atguigu.gulimall.search.vo;

import com.atguigu.common.to.es.SkuEsModel;
import lombok.Data;

import java.util.List;

@Data
public class SearchResult {
    // 查询到的所有商品信息
    private List<SkuEsModel> skuEsModels;

    // 以下是分页信息
    private Integer pageNum;// 当前页码
    private Integer totalPages;// 总页码
    private Long totalRecords;// 总记录数

    private List<BrandVo> brandVos;
    @Data
    public static class BrandVo{
        private Long brandId;
        private String brandName;
        private String brandImg;
    }

    // 当前查询结果所涉及的分类
    private List<CatalogVo> catalogVos;
    @Data
    public static class CatalogVo{
        private Long catalogId;
        private String catalogName;
    }

    // 当前查询结果所涉及的属性
    private List<Attrs> attrs;
    @Data
    public static class Attrs{
        private Long attrId;
        private String attrName;
        private List<String> attrValue;
    }

}
