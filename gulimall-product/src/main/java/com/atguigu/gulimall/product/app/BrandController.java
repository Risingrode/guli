package com.atguigu.gulimall.product.app;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.atguigu.common.valid.AddGroup;
import com.atguigu.common.valid.UpdateGroup;
import com.atguigu.common.valid.UpdateStatusGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.atguigu.gulimall.product.entity.BrandEntity;
import com.atguigu.gulimall.product.service.BrandService;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.R;


/**
 * 品牌
 *
 * @author fuchangwei
 * @email 3185087246@qq.com
 * @date 2023-07-24 15:34:33
 */

// 参数校验机制，与@NotNull、@NotBlank、@Min、@Max 这些注解配合使用，用于对请求参数进行数据验证，确保数据的合法性。
@Validated
@RestController
@RequestMapping("product/brand")
public class BrandController {
    @Autowired
    private BrandService brandService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params) {
        // System.out.println(params);
        // PageUtils 封装好的后端工具
        PageUtils page = brandService.queryPage(params);
        return R.ok().put("page", page);
    }

    /**
     * TODO : 查询品牌名称
     */
    @RequestMapping("/info/{brandId}")
    public R info(@PathVariable("brandId") Long brandId) {
        BrandEntity brand = brandService.getById(brandId);

        return R.ok().put("brand", brand);
    }

    @RequestMapping("/info/ids")
    public R infoByIds(@PathVariable("brandIds") List<Long> brandIds) {
        List<BrandEntity> brandEntities = brandService.queryByIds(brandIds);

        return R.ok().put("brand", brandEntities);
    }

    /**
     * 保存  JSP校验
     */
    @RequestMapping("/save")
    // @Valid 唤醒校验功能
    // 这个空接口的用处: 用于标识当前校验属于哪个分组
    public R save(@Validated(AddGroup.class) @RequestBody BrandEntity brand) {
//        result 封装了校验结果
//        if(result.hasErrors()){
//            Map<String,String> map=new HashMap<>();
//            // 获取校验的错误结果
//            result.getFieldErrors().forEach((item)->{
//                // 获取错误信息
//               String message=item.getDefaultMessage();
//               // 获取错误的属性名字
//                String name= item.getField();
//                map.put(name,message);
//            });
//            return R.error(400,"提交的数据不合法").put("data",map);
//        }else{
//
//        }
        brandService.save(brand);
        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    //  UpdateGroup.class 在common文件里面，它只是一个标识
    public R update(@Validated(UpdateGroup.class) @RequestBody BrandEntity brand) {
        // 进行冗余字段的更新
        // 因为有关联的表，所以需要进行级联更新   A表是B表和C表的汇总, 如果B表改变了，A表关联的数据也要改变
        brandService.updateDetails(brand);
        return R.ok();
    }

    /**
     * 修改状态
     */
    @RequestMapping("/update/status")
    //  把前端数据转化成下面这样： Brand{name='Nike', description='Sports apparel and equipment', country='USA'}
    public R updateStatus(@Validated(UpdateStatusGroup.class) @RequestBody BrandEntity brand) {
        brandService.updateById(brand);
        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("product:brand:delete")
    public R delete(@RequestBody Long[] brandIds) {
        brandService.removeByIds(Arrays.asList(brandIds));
        return R.ok();
    }

}
