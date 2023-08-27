package com.atguigu.common.to.es;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/*

*/

@Data
public class SkuEsModel {
    private Long skuId;// sku是什么？ 库存量单位
    private Long spuId;// spuId是什么？ 商品id
    private String skuTitle;
    private BigDecimal skuPrice;
    private String skuImg;
    private Long saleCount;
    private Boolean hasStock;// 是否有库存
    private Long hotScore;// 热度评分
    private Long brandId;// 品牌id
    private Long catalogId;// 分类id
    private String brandName;// 品牌名
    private String brandImg;// 品牌图片
    private String catalogName;// 分类名
    private List<Object> attrs;// 销售属性组合

    // 检索属性
    @Data
    public static class Attrs {
        private Long attrId;
        private String attrName;
        private String attrValue;
    }
}
