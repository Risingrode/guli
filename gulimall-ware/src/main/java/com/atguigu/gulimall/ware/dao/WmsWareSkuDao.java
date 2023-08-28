package com.atguigu.gulimall.ware.dao;

import com.atguigu.gulimall.ware.entity.WmsWareSkuEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 商品库存
 * 
 * @author Changwei Fu
 * @email 3185087246@qq.com
 * @date 2023-07-25 10:15:32
 */
@Mapper
public interface WmsWareSkuDao extends BaseMapper<WmsWareSkuEntity> {

    void addStock(@Param("skuId") Long skuId, @Param("wareId") Long wareId, @Param("skuNum") Integer skuNum);

    Long getSkuStock(Long item);

}
