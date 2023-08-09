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
    public void received(List<Long> ids) {// 采购单id
        // 1. 确认当前采购单是新建还是已分配状态
        List<WmsPurchaseEntity> collect = ids.stream().map(id -> {
            WmsPurchaseEntity byId = this.getById(id);
            return byId;
        }).filter(item -> {
            if (item.getStatus() == WareConstant.PurchaseStatusEnum.CREATED.getCode()
                    || item.getStatus() == WareConstant.PurchaseStatusEnum.ASSIGNED.getCode()) {
                return true;
            }
            return false;
        }).map(item->{
            item.setStatus(WareConstant.PurchaseStatusEnum.RECEIVE.getCode());
            item.setUpdateTime(new Date());
            return item;
        }).collect(Collectors.toList());
        // 2. 改变采购单状态
        this.updateBatchById(collect);
        // 3. 改变采购项状态
        collect.forEach(item->{
            List<WmsPurchaseDetailEntity>list= detailService.listDetailByPurchaseId(item.getId());
            List<WmsPurchaseDetailEntity> collect1 = list.stream().map(entity -> {
                WmsPurchaseDetailEntity entity1 = new WmsPurchaseDetailEntity();
                entity1.setId(entity.getId());
                entity1.setStatus(WareConstant.PurchaseDetailStatusEnum.BUYING.getCode());
                return entity1;
            }).collect(Collectors.toList());
            detailService.updateBatchById(collect1);
        });

    }

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
        // 这里的id报错
        if(mergeVo.getPurchaseId()==null){
            WmsPurchaseEntity entity = new WmsPurchaseEntity();
            entity.setStatus(WareConstant.PurchaseStatusEnum.CREATED.getCode());
            entity.setCreateTime(new Date());
            entity.setUpdateTime(new Date());
            this.save(entity);
            mergeVo.setPurchaseId(entity.getId());
        }

//        Long id = (Long) mergeVo.getPurchaseId();
//        if(id==null){
//            // 新建
//            WmsPurchaseEntity entity = new WmsPurchaseEntity();
//            entity.setStatus(WareConstant.PurchaseStatusEnum.CREATED.getCode());
//            entity.setCreateTime(new Date());
//            entity.setUpdateTime(new Date());
//            this.save(entity);
//            id= entity.getId();
//        }
        // TODO： 确认采购单的状态是0,1才可以合并

        List<Long> item=mergeVo.getItem();

        Long finalId = mergeVo.getPurchaseId();
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
        purchaseEntity.setId(finalId);
        purchaseEntity.setUpdateTime(new Date());
        this.updateById(purchaseEntity);

    }

}