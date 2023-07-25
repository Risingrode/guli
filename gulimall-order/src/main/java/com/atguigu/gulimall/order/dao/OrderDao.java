package com.atguigu.gulimall.order.dao;

import com.atguigu.gulimall.order.entity.OrderEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单
 * 
 * @author Changwei Fu
 * @email 3185087246@qq.com
 * @date 2023-07-25 09:57:51
 */
@Mapper
public interface OrderDao extends BaseMapper<OrderEntity> {
	
}
