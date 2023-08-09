package com.atguigu.gulimall.ware.feign;

import com.atguigu.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

// 远程调用是个接口
@FeignClient("gulimall-product")
public interface ProductFeignService {
    /**
     * 1)、让所有请求过网关；
     * 1、@FeignClient("gulimall-gateway")：给gulimall-gateway所在的机器发请求
     * 2、/api/product/skuinfo/info/{skuId}：给gulimall-gateway的/api/product/skuinfo/info/{skuId}发请求
     * 3、如果调用失败，自动进行重试
     * 4、开启日志功能
     */

    // TODO： 这里需要修改，原本是没有返回值的，现在需要返回R
    @RequestMapping("product/skuinfo/info/{skuId}")
    public R info(@PathVariable("skuId") Long skuId);


}
