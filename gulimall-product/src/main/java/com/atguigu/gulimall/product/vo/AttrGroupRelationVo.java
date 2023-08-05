package com.atguigu.gulimall.product.vo;

import lombok.Data;

@Data
public class AttrGroupRelationVo {
    // 主要是用来收集属性的id,以及属性分组的id
    private Long attrId;
    private Long attrGroupId;
}
