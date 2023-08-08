package com.atguigu.gulimall.ware.service.impl;

import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;

import com.atguigu.gulimall.ware.dao.WmsWareSkuDao;
import com.atguigu.gulimall.ware.entity.WmsWareSkuEntity;
import com.atguigu.gulimall.ware.service.WmsWareSkuService;
import org.springframework.util.StringUtils;


@Service("wmsWareSkuService")
public class WmsWareSkuServiceImpl extends ServiceImpl<WmsWareSkuDao, WmsWareSkuEntity> implements WmsWareSkuService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        // skuId : 1
        // wareId : 2
        QueryWrapper<WmsWareSkuEntity> wrapper = new QueryWrapper<>();
        String skuId=(String) params.get("skuId");
        if (!StringUtils.isEmpty(skuId)){
            wrapper.eq("sku_id",skuId);
        }

        String wareId = (String) params.get("wareId");
        if (!StringUtils.isEmpty(skuId)){
            wrapper.eq("ware_id",wareId);
        }

        IPage<WmsWareSkuEntity> page = this.page(
                new Query<WmsWareSkuEntity>().getPage(params),
                wrapper
        );

        return new PageUtils(page);
    }

}