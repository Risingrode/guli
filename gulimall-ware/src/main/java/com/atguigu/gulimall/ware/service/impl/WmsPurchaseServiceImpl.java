package com.atguigu.gulimall.ware.service.impl;

import com.atguigu.common.constant.WareConstant;
import com.atguigu.gulimall.ware.MergeVo;
import com.atguigu.gulimall.ware.entity.WmsPurchaseDetailEntity;
import com.atguigu.gulimall.ware.service.WmsPurchaseDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;

import com.atguigu.gulimall.ware.dao.WmsPurchaseDao;
import com.atguigu.gulimall.ware.entity.WmsPurchaseEntity;
import com.atguigu.gulimall.ware.service.WmsPurchaseService;


@Service("wmsPurchaseService")
public class WmsPurchaseServiceImpl extends ServiceImpl<WmsPurchaseDao, WmsPurchaseEntity> implements WmsPurchaseService {

    @Autowired
    WmsPurchaseDetailService detailService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<WmsPurchaseEntity> page = this.page(
                new Query<WmsPurchaseEntity>().getPage(params),
                new QueryWrapper<WmsPurchaseEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public PageUtils queryPageUnreceive(Map<String, Object> params) {
        //先查询状态，status 只能是0或者是1

        IPage<WmsPurchaseEntity> page = this.page(
                new Query<WmsPurchaseEntity>().getPage(params),
                new QueryWrapper<WmsPurchaseEntity>().eq("status",0).or().eq("status",1)
        );

        return new PageUtils(page);
    }

    // 合并采购需求
    @Override
    public void mergePurchase(MergeVo mergeVo) {
        Long id = mergeVo.getPurchaseId();
        if(id==null){
            // 新建
            WmsPurchaseEntity entity = new WmsPurchaseEntity();
            entity.setStatus(WareConstant.PurchaseStatusEnum.CREATED.getCode());
            entity.setCreateTime(new Date());
            entity.setUpdateTime(new Date());
            this.save(entity);
            id= entity.getId();
        }

        List<Long> item=mergeVo.getItem();

        Long finalId = id;
        List<WmsPurchaseDetailEntity> collect = item.stream().map(i -> {
            WmsPurchaseDetailEntity detailEntity = new WmsPurchaseDetailEntity();
            detailEntity.setId(i);
            detailEntity.setPurchaseId(finalId);
            // 最新状态码
            detailEntity.setStatus(WareConstant.PurchaseDetailStatusEnum.ASSIGNED.getCode());
            return detailEntity;
        }).collect(Collectors.toList());

        detailService.updateBatchById(collect);
        WmsPurchaseEntity purchaseEntity=new WmsPurchaseEntity();
        purchaseEntity.setId(id);
        purchaseEntity.setUpdateTime(new Date());
        this.updateById(purchaseEntity);

    }

}