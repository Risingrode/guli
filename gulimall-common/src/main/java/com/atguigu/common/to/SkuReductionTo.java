package com.atguigu.common.to;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class SkuReductionTo {

    private Long skuId;
    private Integer fullCount;
    private BigDecimal discount;
    private BigDecimal fullPrice;
    private BigDecimal reducePrice;
    private int countStatus;
    private Integer priceStatus;
    private List<MemberPrice> memberPrice;

}
