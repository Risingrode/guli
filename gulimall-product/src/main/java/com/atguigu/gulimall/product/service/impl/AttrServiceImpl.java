package com.atguigu.gulimall.product.service.impl;

import com.atguigu.gulimall.product.dao.AttrAttrgroupRelationDao;
import com.atguigu.gulimall.product.dao.AttrGroupDao;
import com.atguigu.gulimall.product.dao.CategoryDao;
import com.atguigu.gulimall.product.entity.AttrAttrgroupRelationEntity;
import com.atguigu.gulimall.product.entity.AttrGroupEntity;
import com.atguigu.gulimall.product.entity.CategoryEntity;
import com.atguigu.gulimall.product.service.CategoryService;
import com.atguigu.gulimall.product.vo.AttrRespVo;
import com.atguigu.gulimall.product.vo.AttrVo;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;

import com.atguigu.gulimall.product.dao.AttrDao;
import com.atguigu.gulimall.product.entity.AttrEntity;
import com.atguigu.gulimall.product.service.AttrService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.management.relation.Relation;


@Service("attrService")
public class AttrServiceImpl extends ServiceImpl<AttrDao, AttrEntity> implements AttrService {

    @Autowired
    AttrAttrgroupRelationDao relationDao;
    @Autowired
    AttrGroupDao attrGroupDao;
    @Autowired
    CategoryDao categoryDao;
    @Autowired
    CategoryService categoryService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<AttrEntity> page = this.page(
                new Query<AttrEntity>().getPage(params),
                new QueryWrapper<AttrEntity>()
        );

        return new PageUtils(page);
    }

    @Transactional
    @Override
    public void saveAttr(AttrVo attr) {
        // 1.保存基本数据
        AttrEntity attrEntity = new AttrEntity();
        // 复制属性
        BeanUtils.copyProperties(attr, attrEntity);
        this.save(attrEntity);
        // 2.保存关联关系
        AttrAttrgroupRelationEntity relationEntity = new AttrAttrgroupRelationEntity();
        // 设置分组，设置属性id
        relationEntity.setAttrGroupId(attr.getAttrGroupId());
        relationEntity.setAttrId(attrEntity.getAttrId());
        // 保存关联关系
        relationDao.insert(relationEntity);

    }

    @Override
    public PageUtils queryBaseAttrPage(Map<String, Object> params, Long categoryId) {
        QueryWrapper<AttrEntity> wrapper = new QueryWrapper<>();
        if (categoryId != 0) {
            // 0表示查询所有
            // 不等于0表示查询指定分类下的属性
            wrapper.eq("catelog_id", categoryId);
        }
        // 对传递过来的值进行模糊查询
        String key = (String) params.get("key");
        if (!StringUtils.isEmpty(key)) {
            // select * from pms_attr where attr_id = key or attr_name like %key%
            wrapper.and((w) -> {
                w.eq("attr_id", key).or().like("attr_name", key);
            });
        }
        IPage<AttrEntity> page = this.page(new Query<AttrEntity>().getPage(params),
                wrapper.eq("attr_type", "base")
                        .eq("catelog_id", categoryId));
        PageUtils pageUtils = new PageUtils(page);
        List<AttrEntity> records = page.getRecords();
        records.stream().map((item) -> {
            AttrRespVo respVo = new AttrRespVo();
            BeanUtils.copyProperties(item, respVo);
            // 设置分组和分类的名字

            AttrAttrgroupRelationEntity attrId = relationDao.selectOne(new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_id", item.getAttrId()));
            if (attrId != null) {
                AttrGroupEntity attrGroupEntity = attrGroupDao.selectById(attrId.getAttrGroupId());
                respVo.setGroupName(attrGroupEntity.getAttrGroupName());
            }
            CategoryEntity categoryEntity = categoryDao.selectById(item.getCatelogId());
            if (categoryEntity != null) {
                respVo.setCatelogName(categoryEntity.getName());
            }
            return respVo;
        }).collect(Collectors.toList());
        pageUtils.setList(records);
        return pageUtils;
    }

    @Override
    public AttrRespVo getAttrInfo(Long attrId) {
        AttrRespVo respVo = new AttrRespVo();
        AttrEntity attrEntity = this.getById(attrId);
        BeanUtils.copyProperties(attrEntity, respVo);
        // 设置分组和分类的名字
        // 1.从关联表中查询分组id 设置分组信息
        AttrAttrgroupRelationEntity attrId1 = relationDao.selectOne(new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_id", attrId));
        if (attrId1 != null) {
            respVo.setAttrGroupId(attrId1.getAttrGroupId());
            AttrGroupEntity attrGroupEntity = attrGroupDao.selectById(attrId1.getAttrGroupId());
            if (attrGroupEntity != null) {
                respVo.setGroupName(attrGroupEntity.getAttrGroupName());
            }
        }
        // 2.设置分类信息
        Long catelogId = attrEntity.getCatelogId();
        Long[] catelogPath = categoryService.findCatelogPath(catelogId);
        respVo.setCatelogPath(catelogPath);
        CategoryEntity categoryEntity = categoryDao.selectById(attrEntity.getCatelogId());
        if (categoryEntity != null) {
            respVo.setCatelogName(categoryEntity.getName());
        }
        return respVo;
    }

    @Transactional
    @Override
    public void updateAttr(AttrVo attr) {
        AttrEntity attrEntity = new AttrEntity();
        BeanUtils.copyProperties(attr, attrEntity);
        this.updateById(attrEntity);
        // 修改分组关联
        AttrAttrgroupRelationEntity relationEntity = new AttrAttrgroupRelationEntity();
        // AttrGroupId 是什么？ 从前端传递过来的 分组id
        // AttrID 是什么？ 从前端传递过来的 属性id
        relationEntity.setAttrGroupId(attr.getAttrGroupId());
        relationEntity.setAttrId(attrEntity.getAttrId());
        // 这里是看分组id 有没有，有的话就是修改，没有的话就是新增
        Integer count = relationDao.selectCount(new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_id", attr.getAttrId()));

        if (count > 0) { // 大于0，就是修改操作
            relationDao.update(relationEntity, new UpdateWrapper<AttrAttrgroupRelationEntity>().eq("attr_id", attr.getAttrId()));
        } else { // 没有就是新增
            relationDao.insert(relationEntity);
        }
    }
}