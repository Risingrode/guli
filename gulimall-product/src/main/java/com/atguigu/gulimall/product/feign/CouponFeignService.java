package com.atguigu.gulimall.product.feign;

import com.atguigu.common.to.SkuReductionTo;
import com.atguigu.common.to.SpuBoundTo;
import com.atguigu.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient("gulimall-coupon")
public interface CouponFeignService {
    /*
    1. CouponFeignService.saveSpuBounds()是一个远程调用，调用的是gulimall-coupon服务的SpuBoundsController.save()方法
        1.1 gulimall-coupon服务的SpuBoundsController.save()方法的返回值是R类型，所以CouponFeignService.saveSpuBounds()方法的返回值也是R类型
        1.2 对方服务收到请求后，会将R类型的数据转换为json格式的数据，然后返回给调用方
    2. 只要json数据模型是兼容的，双方服务无需使用同一个to类
    */
    @PostMapping("/coupon/spubounds/save")
    R saveSpuBounds(@RequestBody SpuBoundTo spuBoundTo);

    @PostMapping("/coupon/skufullreduction/saveinfo")
    R saveSkuReduction(SkuReductionTo skuReductionTo);

}
















