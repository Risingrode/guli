package com.atguigu.gulimall.product.app;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.atguigu.gulimall.product.entity.BrandEntity;
import com.atguigu.gulimall.product.vo.BrandVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.atguigu.gulimall.product.entity.CategoryBrandRelationEntity;
import com.atguigu.gulimall.product.service.CategoryBrandRelationService;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.R;



/**
 * 品牌分类关联
 *
 * @author Changwei Fu
 * @email 3185087246@qq.com
 * @date 2023-07-31 08:58:41
 */

@RestController
@RequestMapping("product/categorybrandrelation")
public class CategoryBrandRelationController {
    @Autowired
    private CategoryBrandRelationService categoryBrandRelationService;

    /**
     *  获取当前品牌关联的所有分类列表
     */
    @GetMapping ("/catelog/list")
    // @RequestParam("brandId") 从请求参数中获取brandId的参数，传递给Long brandId
    public R cateloglist(@RequestParam("brandId") Long brandId){
        // 从数据库中进行查找，这个数据库的名字叫做pms_category_brand_relation
        List<CategoryBrandRelationEntity> data = categoryBrandRelationService.list(
                new QueryWrapper<CategoryBrandRelationEntity>().eq("brand_id", brandId));
        return R.ok().put("data", data);
    }
    /**
    *  保存品牌和分类的关联关系
    */
    @RequestMapping("/save")
    // 前端传过来的数据是： {catelogId: 1, brandId: 1}
    // 直接记录在数据库(pms_category_brand_relation)中，不需要返回数据
    public R save(@RequestBody CategoryBrandRelationEntity categoryBrandRelation){
        categoryBrandRelationService.saveDetail(categoryBrandRelation);
        return R.ok();
    }

    /**
     * 列表
     */
    @RequestMapping("/list")
    // Map 用于存储多个键值对的数据，Map中的每个键值对都包含一个键和一个值，Map中的键是不能重复的
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = categoryBrandRelationService.queryPage(params);
        return R.ok().put("page", page);
    }

    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
		CategoryBrandRelationEntity categoryBrandRelation = categoryBrandRelationService.getById(id);
        return R.ok().put("categoryBrandRelation", categoryBrandRelation);
    }

    //1. Controller: 处理请求，接收和校验数据
    //2. Service: 接收Controller传过来的请求，进行业务处理
    //3. Controller接收Service处理完的数据，封装页面指定的数据
    // product/categorybrandrelation/brands/list
    @GetMapping("/brands/list")
    public R relationBrandList(@RequestParam(value = "catId",required=true)Long catId){
        List<BrandEntity>vos= categoryBrandRelationService.getBrandsByCatId(catId);
        // 数据过滤，拿到自己想要的数据
        List<BrandVo> collect = vos.stream().map(item -> {
            BrandVo brandVo = new BrandVo();
            brandVo.setBrandId(item.getBrandId());
            brandVo.setBrandName(item.getName());
            return brandVo;
        }).collect(Collectors.toList());

        return R.ok().put("data",collect);
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody CategoryBrandRelationEntity categoryBrandRelation){
		categoryBrandRelationService.updateById(categoryBrandRelation);
        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids){
		categoryBrandRelationService.removeByIds(Arrays.asList(ids));
        return R.ok();
    }

}
