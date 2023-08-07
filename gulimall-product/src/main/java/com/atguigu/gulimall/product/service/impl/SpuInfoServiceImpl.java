package com.atguigu.gulimall.product.service.impl;

import com.atguigu.gulimall.product.entity.*;
import com.atguigu.gulimall.product.service.*;
import com.atguigu.gulimall.product.vo.*;
import org.springframework.beans.BeanUtils;
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

import com.atguigu.gulimall.product.dao.SpuInfoDao;
import org.springframework.transaction.annotation.Transactional;


@Service("spuInfoService")
public class SpuInfoServiceImpl extends ServiceImpl<SpuInfoDao, SpuInfoEntity> implements SpuInfoService {
    @Autowired
    SpuInfoDescService spuInfoDescService;
    @Autowired
    SpuImagesService spuImagesService;
    @Autowired
    AttrService attrService;
    @Autowired
    ProductAttrValueService productAttrValueService;
    @Autowired
    SkuInfoService skuInfoService;
    @Autowired
    SkuImagesService skuImagesService;
    @Autowired
    SkuSaleAttrValueService skuSaleAttrValueService;


    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SpuInfoEntity> page = this.page(
                new Query<SpuInfoEntity>().getPage(params),
                new QueryWrapper<SpuInfoEntity>()
        );

        return new PageUtils(page);
    }

    @Transactional
    @Override
    public void saveSpuInfo(SpuSaveVo vo) {
        //1. 保存spu基本信息 pms_spu_info
        // spu是什么？ spu是商品的集合，比如：华为手机，华为手机就是一个spu，华为手机+华为手环就是一个spu
        SpuInfoEntity spuInfoEntity = new SpuInfoEntity();
        BeanUtils.copyProperties(vo, spuInfoEntity);
        spuInfoEntity.setUpdateTime(new Date());
        spuInfoEntity.setCreateTime(new Date());
        this.saveBaseSpuInfo(spuInfoEntity);

        //2. 保存spu的描述图片 pms_spu_info_desc
        List<String> decript = vo.getDecript();
        SpuInfoDescEntity descEntity = new SpuInfoDescEntity();
        descEntity.setSpuId(spuInfoEntity.getId());
        // 使用逗号分隔符将集合转换为字符串
        descEntity.setDecript(String.join(",", decript));
        //  保存spu的描述图片 pms_spu_info_desc
        spuInfoDescService.saveSpuInfoDesc(descEntity);

        //3。保存spu的图片集 pms_spu_images
        List<String> images = vo.getImages();
        // 保存哪个商品的图片
        spuImagesService.saveImages(spuInfoEntity.getId(), images);

        //4. 保存spu的规格参数 pms_product_attr_value
        List<BaseAttrs> baseAttrs = vo.getBaseAttrs();
        // 保存哪个商品的规格参数
        List<ProductAttrValueEntity> collect = baseAttrs.stream().map((baseAttr) -> {
            ProductAttrValueEntity attrValueEntity = new ProductAttrValueEntity();
            attrValueEntity.setAttrId(baseAttr.getAttrId());
            AttrEntity byId = attrService.getById(baseAttr.getAttrId());
            attrValueEntity.setAttrName(byId.getAttrName());
            attrValueEntity.setAttrValue(baseAttr.getAttrValues());
            attrValueEntity.setQuickShow(baseAttr.getShowDesc());
            attrValueEntity.setSpuId(spuInfoEntity.getId());
            return attrValueEntity;
        }).collect(Collectors.toList());
        productAttrValueService.saveProductAttr(collect);

        //5. 保存当前spu所有的sku信息
        //5.1 sku的基本信息： pms_sku_info
        List<Skus> skus = vo.getSkus();
        if(skus!=null&&skus.size()>0){
            skus.stream().forEach((item)->{
                String defaultImg = "";
                for(Images img : item.getImages()){
                    if(img.getDefaultImg()==1){
                        defaultImg = img.getImgUrl();
                    }
                }
                // item是一个sku的信息
                SkuInfoEntity skuInfoEntity = new SkuInfoEntity();
                BeanUtils.copyProperties(item,skuInfoEntity);
                skuInfoEntity.setBrandId(spuInfoEntity.getBrandId());
                // CatalogId 是什么？ CatalogId是分类id
                skuInfoEntity.setCatalogId(spuInfoEntity.getCatalogId());
                skuInfoEntity.setSaleCount(0L);
                skuInfoEntity.setSpuId(spuInfoEntity.getId());
                skuInfoService.saveSkuInfo(skuInfoEntity);

                // 5.2 sku的图片信息： pms_sku_images
                // sku的名字是什么？ sku的名字是spu的名字+sku的销售属性
                Long skuId = skuInfoEntity.getSkuId();// 自增主键
                List<SkuImagesEntity> imagesEntities = item.getImages().stream().map((img) -> {
                    SkuImagesEntity skuImagesEntity = new SkuImagesEntity();
                    skuImagesEntity.setSkuId(skuId);
                    skuImagesEntity.setImgUrl(img.getImgUrl());
                    skuImagesEntity.setDefaultImg(img.getDefaultImg());
                    return skuImagesEntity;
                }).collect(Collectors.toList());
                skuImagesService.saveBatch(imagesEntities);

                //5.3 sku的销售属性： pms_sku_sale_attr_value
                List<Attr> attr = item.getAttr();
                List<SkuSaleAttrValueEntity> skuSaleAttrValueEntities = attr.stream().map((attrItem) -> {
                    SkuSaleAttrValueEntity skuSaleAttrValueEntity = new SkuSaleAttrValueEntity();
                    BeanUtils.copyProperties(attrItem, skuSaleAttrValueEntity);
                    skuSaleAttrValueEntity.setSkuId(skuId);
                    return skuSaleAttrValueEntity;
                }).collect(Collectors.toList());
                skuSaleAttrValueService.saveBatch(skuSaleAttrValueEntities);
            });
        }
        // 远程服务
        //5.4 sku的优惠满减： gulimall-sms-> sms_sku_lodder(优惠表)-> sms_sku_reduction(满减表)-> sms_member_price(价格表)-> sms_spu_bounds(积分信息)


    }

    @Override
    public void saveBaseSpuInfo(SpuInfoEntity spuInfoEntity) {
        this.baseMapper.insert(spuInfoEntity);
    }


}