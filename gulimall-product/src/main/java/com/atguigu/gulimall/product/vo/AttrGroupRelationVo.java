package com.atguigu.gulimall.product.vo;

import lombok.Data;

@Data
public class AttrGroupRelationVo {
    // 这个属性在那个组中
    private Long attrId;
    // 属性分组id
    private Long attrGroupId;
}
