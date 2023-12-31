package com.atguigu.gulimall.product.dao;

import com.atguigu.gulimall.product.entity.CategoryEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品三级分类
 * 
 * @author fuchangwei
 * @email 3185087246@qq.com
 * @date 2023-07-24 15:34:33
 */
@Mapper
public interface CategoryDao extends BaseMapper<CategoryEntity> {
	
}
