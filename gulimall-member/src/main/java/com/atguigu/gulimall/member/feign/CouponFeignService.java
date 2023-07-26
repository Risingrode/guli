package com.atguigu.gulimall.member.feign;

import com.atguigu.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;

// 找注册中心中的gulimall-coupon服务
@FeignClient("gulimall-coupon")
public interface CouponFeignService {
    // 找到gulimall-coupon服务中的/coupon/coupon/member/list接口
    @RequestMapping("/coupon/coupon/member/list")
    public R memberCoupons();

}
