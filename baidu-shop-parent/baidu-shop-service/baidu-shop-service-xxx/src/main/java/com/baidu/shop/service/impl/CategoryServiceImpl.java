package com.baidu.shop.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baidu.shop.base.BaseApiService;
import com.baidu.shop.base.Result;
import com.baidu.shop.entity.CategoryEntity;
import com.baidu.shop.mapper.CategoryMapper;
import com.baidu.shop.service.CategoryService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.List;

/**
 * @ClassName CategoryServiceImpl
 * @Description: TODO
 * @Author wanghaiyu
 * @Date 2020/8/27
 * @Version V1.0
 **/

@RestController
public class CategoryServiceImpl extends BaseApiService implements CategoryService {

    @Resource
    private CategoryMapper categoryMapper;

    @Override
    public Result<List<CategoryEntity>> getCategoryByPid(Integer pid) {
        CategoryEntity categoryEntity = new CategoryEntity();

        categoryEntity.setParentId(pid);

        List<CategoryEntity> list = categoryMapper.select(categoryEntity);

        return this.setResultSuccess(list);
    }

    @Override
    @Transactional
    public Result<JSONObject> saveCategory(CategoryEntity categoryEntity) {

        //通过新增节点的父id将父节点的parent状态改为1
        CategoryEntity parentCategoryEntity =  new CategoryEntity();
        parentCategoryEntity.setId(categoryEntity.getParentId());
        parentCategoryEntity.setIsParent(1);
        categoryMapper.updateByPrimaryKeySelective(parentCategoryEntity);


        categoryMapper.insertSelective(categoryEntity);

        return this.setResultSuccess();
    }

    @Override
    @Transactional
    public Result<JSONObject> editCategory(CategoryEntity categoryEntity) {

        categoryMapper.updateByPrimaryKeySelective(categoryEntity);

        return this.setResultSuccess();
    }

    @Override
    @Transactional
    public Result<JSONObject> deleteCategory(Integer id) {

        //验证传入的id是否有效
        CategoryEntity categoryEntity = categoryMapper.selectByPrimaryKey(id);
        if (null == categoryEntity) {
            return this.setResultError("当前ID不存在");
        }

        //判断当前删除的节点是否为父节点
        if (categoryEntity.getIsParent() == 1) {
            return this.setResultError("当前节点为父节点,不能被删除");
        }

        //通过当前被删除的节点的parentid查询数据
        Example example = new Example(CategoryEntity.class);
        example.createCriteria().andEqualTo("parentId",categoryEntity.getParentId());
        List<CategoryEntity> list = categoryMapper.selectByExample(example);

        //判断如果查询出的数据只有一条
        if(list.size() == 1){
            //将父节点的isParent改为0
            CategoryEntity parentCategoryEntity= new CategoryEntity();
            parentCategoryEntity.setId(categoryEntity.getParentId());
            parentCategoryEntity.setIsParent(0);
            categoryMapper.updateByPrimaryKeySelective(parentCategoryEntity);
        }


        categoryMapper.deleteByPrimaryKey(id);
        return this.setResultSuccess();
    }


}
