package com.atguigu.gulimall.ware.service.impl;

import ch.qos.logback.core.util.StringCollectionUtil;
import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;

import com.atguigu.gulimall.ware.dao.WmsPurchaseDetailDao;
import com.atguigu.gulimall.ware.entity.WmsPurchaseDetailEntity;
import com.atguigu.gulimall.ware.service.WmsPurchaseDetailService;
import org.springframework.util.StringUtils;


@Service("wmsPurchaseDetailService")
public class WmsPurchaseDetailServiceImpl extends ServiceImpl<WmsPurchaseDetailDao, WmsPurchaseDetailEntity> implements WmsPurchaseDetailService {
    // status: 0 状态
    // wareId: 1 仓库id

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        QueryWrapper<WmsPurchaseDetailEntity> wrapper = new QueryWrapper<>();
        String key = (String) params.get("key");
        if(!StringUtils.isEmpty(key)){
            // purchase_id sku_id
            wrapper.and(w->{
                w.eq("purchase_id",key).or().eq("sku_id",key);
            });
        }

        String status = (String) params.get("status");
        if(!StringUtils.isEmpty(status)){
            // purchase_id sku_id
            wrapper.eq("status",status);
        }

        String wareId = (String) params.get("wareId");
        if(!StringUtils.isEmpty(wareId)){
            // purchase_id sku_id
            wrapper.eq("ware_id",wareId);
        }

        IPage<WmsPurchaseDetailEntity> page = this.page(
                new Query<WmsPurchaseDetailEntity>().getPage(params),
                wrapper
        );

        return new PageUtils(page);
    }

}