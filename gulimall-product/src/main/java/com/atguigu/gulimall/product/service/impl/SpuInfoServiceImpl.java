package com.atguigu.gulimall.product.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.atguigu.common.constant.ProductConstant;
import com.atguigu.common.to.SkuHasStockVo;
import com.atguigu.common.to.SkuReductionTo;
import com.atguigu.common.to.SpuBoundTo;
import com.atguigu.common.to.es.SkuEsModel;
import com.atguigu.common.utils.R;
import com.atguigu.gulimall.product.entity.*;
import com.atguigu.gulimall.product.feign.CouponFeignService;
import com.atguigu.gulimall.product.feign.SearchFeignService;
import com.atguigu.gulimall.product.feign.WareFeignService;
import com.atguigu.gulimall.product.service.*;
import com.atguigu.gulimall.product.vo.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;

import com.atguigu.gulimall.product.dao.SpuInfoDao;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;


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
    @Autowired
    CouponFeignService couponFeignService;
    @Autowired
    BrandService brandService;
    @Autowired
    CategoryService categoryService;
    @Autowired
    WareFeignService wareFeignService;
    @Autowired
    SearchFeignService searchFeignService;




    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SpuInfoEntity> page = this.page(
                new Query<SpuInfoEntity>().getPage(params),
                new QueryWrapper<SpuInfoEntity>()
        );

        return new PageUtils(page);
    }

    // TODO : 高级部分再来看
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

        //5. 保存spu的积分信息
        //5.4 sms_spu_bounds(积分信息) 远程调用保存该信息
        Bounds bounds = vo.getBounds();// 拿到成长积分，购物积分
        // 5.4.1 把要传输的数据封装成对象   product传输数据给coupon
        SpuBoundTo spuBoundTo = new SpuBoundTo();

        BeanUtils.copyProperties(bounds, spuBoundTo);
        spuBoundTo.setSpuId(spuInfoEntity.getId());
        R r = couponFeignService.saveSpuBounds(spuBoundTo);// 远程调用 传输数据
        if (r.getCode() != 0) {
            log.error("远程保存spu积分信息失败");
        }

        //6. 保存当前spu所有的sku信息
        List<Skus> skus = vo.getSkus();
        if (skus != null && skus.size() > 0) {
            skus.forEach((item) -> {
                String defaultImg = "";
                for (Images img : item.getImages()) {
                    if (img.getDefaultImg() == 1) {
                        defaultImg = img.getImgUrl();
                    }
                }
                // 6.1 sku的基本信息
                // item是一个sku的信息 pms_sku_info
                SkuInfoEntity skuInfoEntity = new SkuInfoEntity();
                BeanUtils.copyProperties(item, skuInfoEntity);
                skuInfoEntity.setBrandId(spuInfoEntity.getBrandId());
                // CatalogId 是什么？ CatalogId是分类id
                skuInfoEntity.setCatalogId(spuInfoEntity.getCatalogId());
                skuInfoEntity.setSaleCount(0L);
                skuInfoEntity.setSpuId(spuInfoEntity.getId());
                skuInfoService.saveSkuInfo(skuInfoEntity);

                // 6.2 sku的图片信息： pms_sku_images
                Long skuId = skuInfoEntity.getSkuId();// 自增主键
                List<SkuImagesEntity> imagesEntities = item.getImages().stream().map((img) -> {
                    SkuImagesEntity skuImagesEntity = new SkuImagesEntity();
                    skuImagesEntity.setSkuId(skuId);
                    skuImagesEntity.setImgUrl(img.getImgUrl());
                    skuImagesEntity.setDefaultImg(img.getDefaultImg());
                    return skuImagesEntity;
                }).filter(entity -> {
                    //  没有图片路径，无需保存
                    // 返回true就是需要，返回false就是不需要
                    return !StringUtils.isEmpty(entity.getImgUrl());
                }).collect(Collectors.toList());
                skuImagesService.saveBatch(imagesEntities);


                //6.3 sku的销售属性： pms_sku_sale_attr_value
                List<Attr> attr = item.getAttr();
                List<SkuSaleAttrValueEntity> skuSaleAttrValueEntities = attr.stream().map((attrItem) -> {
                    SkuSaleAttrValueEntity skuSaleAttrValueEntity = new SkuSaleAttrValueEntity();
                    BeanUtils.copyProperties(attrItem, skuSaleAttrValueEntity);
                    skuSaleAttrValueEntity.setSkuId(skuId);
                    return skuSaleAttrValueEntity;
                }).collect(Collectors.toList());
                // 保存sku的销售属性
                skuSaleAttrValueService.saveBatch(skuSaleAttrValueEntities);

                //6.4 sku的优惠满减等多个信息，这里需要使用到远程调用
                // gulimall-sms-> sms_sku_lodder(优惠表)-> sms_sku_reduction(满减表)-> sms_member_price(价格表)-> sms_spu_bounds(积分信息)
                SkuReductionTo skuReductionTo = new SkuReductionTo();
                skuReductionTo.setSkuId(skuId);
                BeanUtils.copyProperties(item, skuReductionTo);
                if (skuReductionTo.getFullCount() > 0 || skuReductionTo.getFullPrice().compareTo(new BigDecimal("0")) == 0) {
                    // 这里是远程调用
                    R r1 = couponFeignService.saveSkuReduction(skuReductionTo);
                    if (r1.getCode() != 0) {
                        log.error("远程保存sku优惠信息失败");
                    }
                }

            });
        }

    }

    @Override
    public void saveBaseSpuInfo(SpuInfoEntity spuInfoEntity) {
        this.baseMapper.insert(spuInfoEntity);
    }

    @Override
    public PageUtils queryPageByCondition(Map<String, Object> params) {
        QueryWrapper<SpuInfoEntity> wrapper = new QueryWrapper<>();
        String key = (String) params.get("key");// 拿到搜索的关键字
        // 不能直接写，要分段写
        if(!StringUtils.isEmpty(key)){
            wrapper.and((obj)->{
                obj.eq("id",params.get("key")).or().like("spu_name",params.get("key"));
            });
        }
        String status = (String) params.get("status");
        if(!StringUtils.isEmpty(status)){
            // 0 1 2 3 这个属性是什么？
            wrapper.eq("publish_status",status);
        }
        String brandId = (String) params.get("brandId");
        if(!StringUtils.isEmpty(brandId)&&!"0".equalsIgnoreCase(brandId)){// 品牌id
            wrapper.eq("brand_id",brandId);
        }
        String catelogId = (String) params.get("catelogId");
        if(!StringUtils.isEmpty(catelogId)&&!"0".equalsIgnoreCase(catelogId)){// 分类id
            wrapper.eq("catalog_id",catelogId);
        }

        IPage<SpuInfoEntity> page = this.page(
                new Query<SpuInfoEntity>().getPage(params),
                wrapper
        );

        return new PageUtils(page);
    }

    @Override
    public void up(Long spuId) {
        // 1. 查询当前spuId对应的所有sku信息，品牌的名字，分类的名字
        List<SkuInfoEntity> skus = skuInfoService.getSkuInfoBySpuId(spuId);
        // 后面进行远程调用会使用到
        List<Long> skuIdList = skus.stream().map(SkuInfoEntity::getSkuId).collect(Collectors.toList());

        // 4.查询当前sku的所有可以被检索的规格属性
        List<ProductAttrValueEntity> baseAttrs = productAttrValueService.baseAttrlistforspu(spuId);
        // 收集到所有属性的id
        List<Long> attrIds = baseAttrs.stream().map(ProductAttrValueEntity::getAttrId).collect(Collectors.toList());
        // 通过属性id查询出所有可以被检索的属性
        List<Long> searchAttrIds = attrService.selectSearchAttrIds(attrIds);
        Set<Long> idSet = new HashSet<>(searchAttrIds);

        List<SkuEsModel.Attrs> attrsList = baseAttrs.stream().filter(item -> {
            // 过滤出可以被检索的属性
            return idSet.contains(item.getAttrId());
        }).map(item -> {
            // 封装成需要的数据
            SkuEsModel.Attrs attr1 = new SkuEsModel.Attrs();
            BeanUtils.copyProperties(item, attr1);
            return attr1;
        }).collect(Collectors.toList());
        // 1. 发送远程调用，检测库存系统是否有库存
        Map<Long, Boolean> stockMap=null;
        try{// 如果远程调用失败
            R r = wareFeignService.getSkusHasStock(skuIdList);
            // 2. 封装每个sku的信息
            TypeReference<List<SkuHasStockVo>> typeReference = new TypeReference<List<SkuHasStockVo>>() {
            };
            stockMap = r.getData(typeReference).stream().collect(Collectors.toMap(SkuHasStockVo::getSkuId, item -> item.getHasStock()));
        }catch (Exception e){
            log.error("库存服务查询异常，原因{}",e);
        }




        // 2. 封装每个sku的信息
        Map<Long, Boolean> finalStockMap = stockMap;// 设置临时变量
        List<SkuEsModel> upProducts = skus.stream().map(sku -> {
            // 组装需要的数据
            SkuEsModel esModel = new SkuEsModel();
            BeanUtils.copyProperties(sku, esModel);
            // 有的名字不太一样，需要重新赋值
            // skuPrice hotScore
            esModel.setSkuPrice(sku.getPrice());
            esModel.setSkuImg(sku.getSkuDefaultImg());
            // hasStock skuStock
            // 1.查询是否具有库存 发送远程调用
            if (finalStockMap == null) {
                esModel.setHasStock(true);
            } else {
                esModel.setHasStock(finalStockMap.get(sku.getSkuId()));
            }
            // 2.热度评分 0
            esModel.setHotScore(0L);
            // 3.查询品牌和分类的名字信息
            BrandEntity brand = brandService.getById(esModel.getBrandId());
            esModel.setBrandName(brand.getName());
            esModel.setBrandImg(brand.getLogo());

            CategoryEntity category = categoryService.getById(esModel.getCatalogId());
            esModel.setCatalogName(category.getName());
            // 4.设置检索属性
            esModel.setAttrs(Collections.singletonList(attrsList));
            return esModel;
        }).collect(Collectors.toList());

        // TODO 5.把数据发送给es进行保存：gulimall-search
        R r = searchFeignService.productStatusUp(upProducts);
        if(r.getCode()==0) {
            // 远程调用成功
            // TODO 6.修改当前spu的状态 为上架
            baseMapper.updateSpuStatus(spuId, ProductConstant.StatusEnum.SPU_UP.getCode());
        }else{
            // 远程调用失败
            // TODO 7.重复调用？接口幂等性，重试机制
            // Feign调用流程
            // 1.构造请求数据，把对象转化为json
            // 2.发送请求进行执行
            // 3.执行亲跪求会有重试器，最大重试次数，每次重试的时间间隔


        }
    }
}
















