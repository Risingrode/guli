package com.atguigu.gulimall.ware.vo;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class PurchaseItemDoneVo {
    // 采购项

    private Long itemId;
    private Integer status;
    private String reason;

}
