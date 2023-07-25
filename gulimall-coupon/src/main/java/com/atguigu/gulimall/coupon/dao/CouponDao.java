package com.atguigu.gulimall.coupon.dao;

import com.atguigu.gulimall.coupon.entity.CouponEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 优惠券信息
 * 
 * @author Changwei Fu
 * @email 3185087246@qq.com
 * @date 2023-07-25 09:46:24
 */
@Mapper
public interface CouponDao extends BaseMapper<CouponEntity> {
	
}
