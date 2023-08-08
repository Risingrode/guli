package com.atguigu.gulimall.ware;

import lombok.Data;

import java.util.List;

@Data
public class MergeVo {
    private Long purchaseId;// 整单id
    private List<Long> item;// 合并集合
}
