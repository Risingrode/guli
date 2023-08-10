package com.atguigu.gulimall.product.vo;

import lombok.Data;

@Data
// 属性响应vo 作用：返回给前端的数据
public class AttrRespVo extends AttrVo{
    // 分类名字
    private String catelogName;
    // 分组名字
    private String groupName;
    // 所属分类完整路径
    private Long[] catelogPath;

}
