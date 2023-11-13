package com.atguigu.gulimall.product.service.impl;

import com.atguigu.gulimall.product.service.CategoryBrandRelationService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;

import com.atguigu.gulimall.product.dao.BrandDao;
import com.atguigu.gulimall.product.entity.BrandEntity;
import com.atguigu.gulimall.product.service.BrandService;


@Service("brandService")
public class BrandServiceImpl extends ServiceImpl<BrandDao, BrandEntity> implements BrandService {

    @Autowired
    CategoryBrandRelationService categoryBrandRelationService;

    @Override
    // 前端传过来的品牌id,或者名字，可能会有个key值
    public PageUtils queryPage(Map<String, Object> params) {
        // 获取key
        String key = (String) params.get("key");
        // 如果key不为空，就拼接查询条件
        QueryWrapper<BrandEntity> queryWrapper =new QueryWrapper<>();
        if (!StringUtils.isEmpty(key)) {
            // 品牌id 或者 名字
            queryWrapper.like("brand_id", key).or().like("name", key);
        }
        IPage<BrandEntity> page = this.page(
                // 第一个参数 new Query<BrandEntity>().getPage(params) 是用于生成分页信息的查询条件。Query 是一个工具类，用于构建查询条件，.getPage(params) 是从请求参数中提取分页信息，生成一个分页对象。
                //第二个参数 queryWrapper 是一个查询条件的封装器，用于构建具体的查询条件。
                new Query<BrandEntity>().getPage(params),
                queryWrapper
        );
        return new PageUtils(page);
    }

    @Override
    public void updateDetails(BrandEntity brand) {
        // 保证冗余字段的数据一致性
        this.updateById(brand);
        // 更新其他关联表的数据
        if(!StringUtils.isEmpty(brand.getName())){
            // 更新其他表的name 品牌id 和 品牌名字
            categoryBrandRelationService.updateBrand(brand.getBrandId(), brand.getName());
            // TODO: 更新其他表的brand_img


        }

    }

    @Override
    public List<BrandEntity> queryByIds(List<Long> brandIds) {
        return this.baseMapper.selectList(new QueryWrapper<BrandEntity>().in("brand_id", brandIds));
    }

}




















