/**
 * Copyright 2019 bejson.com
 */
package com.atguigu.gulimall.product.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * Auto-generated: 2019-11-26 10:50:34
 *
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 */
@Data
// setSpuName("时尚T恤");
// 每个 Spu 对应着商品的一个具体属性组合，例如颜色、尺寸、款式等。
public class SpuSaveVo {
    // 这个是大范围 比如 衣服
    private String spuName;
    private String spuDescription;
    // 商品所属的分类id
    private Long catalogId;
    private Long brandId;
    private BigDecimal weight;
    // 商品的发布状态
    private int publishStatus;
    private List<String> decript;
    private List<String> images;
    // 积分
    private Bounds bounds;
    // 基本属性列表
    private List<BaseAttrs> baseAttrs;
    // 库存单位列表
    private List<Skus> skus;

}