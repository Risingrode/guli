package com.atguigu.gulimall.ware.service.impl;

import com.atguigu.common.utils.R;
import com.atguigu.gulimall.ware.feign.ProductFeignService;
import com.atguigu.gulimall.ware.vo.SkuHasStockVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    @Autowired
    WmsWareSkuDao wmsWareSkuDao;
    @Autowired
    ProductFeignService productFeignService;


    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        // skuId : 1
        // wareId : 2
        QueryWrapper<WmsWareSkuEntity> wrapper = new QueryWrapper<>();
        String skuId = (String) params.get("skuId");
        if (!StringUtils.isEmpty(skuId)) {
            wrapper.eq("sku_id", skuId);
        }

        String wareId = (String) params.get("wareId");
        if (!StringUtils.isEmpty(skuId)) {
            wrapper.eq("ware_id", wareId);
        }

        IPage<WmsWareSkuEntity> page = this.page(
                new Query<WmsWareSkuEntity>().getPage(params),
                wrapper
        );

        return new PageUtils(page);
    }

    @Override
    public void addStock(Long skuId, Long wareId, Integer skuNum) {
        // 1. 判断如果没有这个库存记录，就新增
        // select * from wms_ware_sku where sku_id = ? and ware_id = ?
        List<WmsWareSkuEntity> entities = wmsWareSkuDao.selectList(new QueryWrapper<WmsWareSkuEntity>().eq("sku_id", skuId).eq("ware_id", wareId));
        if (entities == null || entities.size() == 0) {
            // 没有这个库存记录
            WmsWareSkuEntity entity = new WmsWareSkuEntity();
            entity.setSkuId(skuId);
            entity.setWareId(wareId);
            entity.setStock(skuNum);
            entity.setStockLocked(0);// 默认采购单锁定的库存为0
            // TODO 远程查询sku的名字，如果失败，整个事务无需回滚
            // 1.自己cache掉异常
            // 2.让这个方法的异常不要回滚事务  @Transactional(noRollbackFor = Exception.class)
            // feign远程查询sku的名字
            try {
                R info = productFeignService.info(skuId);
                Map<String, Object> data = (Map<String, Object>) info.get("skuInfo");
                if (info.getCode() == 0) {
                    entity.setSkuName((String) data.get("skuName"));
                }
            } catch (Exception e) {
                log.error("远程查询sku的名字失败", e);
            }
            wmsWareSkuDao.insert(entity);
        } else {
            // 有这个库存记录
            wmsWareSkuDao.addStock(skuId, wareId, skuNum);
        }


    }

    @Override
    public List<SkuHasStockVo> getSkusHasStock(List<Long> skuIds) {
        //
        List<SkuHasStockVo> collect = skuIds.stream().map(item -> {
            // 1.查询当前sku的总库存量
            SkuHasStockVo vo = new SkuHasStockVo();
            // 库存的总数量减去锁定的库存数量（锁定的库存：别人下单被占用了）
            // select sum(stock - stock_locked) from wms_ware_sku where sku_id = ?
            Long count=baseMapper.getSkuStock(item);
            vo.setSkuId(item);
            vo.setHasStock(count != null && count > 0);
            return vo;
        }).collect(Collectors.toList());


        return null;
    }

}















