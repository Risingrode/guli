package com.atguigu.gulimall.product.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor // 全参构造器
@NoArgsConstructor // 无参构造器
public class Catelog2Vo {
    private String catalog1Id;// 1级父分类id
    private List<Catelog2Vo.Catelog3Vo> category3List;// 三级子分类
    private String id;//当前节点id值
    private String name;//当前节点名称

    @Data
    @AllArgsConstructor // 全参构造器
    @NoArgsConstructor // 无参构造器
    public static class Catelog3Vo {
        private String catalog2Id;// 2级父分类id
        private String id;//当前节点id值
        private String name;//当前节点名称
    }

}
