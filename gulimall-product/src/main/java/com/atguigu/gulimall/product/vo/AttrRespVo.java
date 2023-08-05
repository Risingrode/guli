package com.atguigu.gulimall.product.vo;

import lombok.Data;

@Data
public class AttrRespVo extends AttrVo{
    // 分类名字
    private String catelogName;
    // 分组名字
    private String groupName;
    // 所属分类完整路径
    private Long[] catelogPath;

}
