/**
  * Copyright 2019 bejson.com 
  */
package com.atguigu.gulimall.product.vo;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author fcw
 */

@Data
// sku : setSkuName("T恤 - 红色，L号");
public class Skus {
    // 这个是小范围 比如： 
    // 属性列表，表示 仓库 的属性，例如颜色、尺寸等。
    private List<Attr> attr;
    // 仓库名字
    private String skuName;
    private BigDecimal price;
    private String skuTitle;
    // 副标题
    private String skuSubtitle;
    private List<Images> images;
    // 描述列表
    private List<String> descar;
    // 满减条件中的满足数量。
    private int fullCount;
    // 满减条件中的折扣金额。
    private BigDecimal discount;
    // 商品数量状态
    private int countStatus;
    // 满减条件中的满足金额。
    private BigDecimal fullPrice;
    // 满减条件中的减免金额。
    private BigDecimal reducePrice;
    // 价格状态
    private int priceStatus;
    // 会员价格列表，表示不同会员的价格信息。
    private List<MemberPrice> memberPrice;
}







