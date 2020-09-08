package com.baidu.shop.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baidu.shop.base.BaseApiService;
import com.baidu.shop.base.Result;
import com.baidu.shop.dto.BrandDTO;
import com.baidu.shop.entity.BrandEntity;
import com.baidu.shop.entity.CategoryBrandEntity;
import com.baidu.shop.mapper.BrandMapper;
import com.baidu.shop.mapper.CategoryBrandMapper;
import com.baidu.shop.service.BrandService;
import com.baidu.shop.utils.BaiDuBeanUtil;
import com.baidu.shop.utils.ObjectUtil;
import com.baidu.shop.utils.PinYinUtil;
import com.baidu.shop.utils.StringUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.gson.JsonObject;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @ClassName BrandServiceImpl
 * @Description: TODO
 * @Author wanghaiyu
 * @Date 2020/8/31
 * @Version V1.0
 **/
@RestController
public class BrandServiceImpl extends BaseApiService implements BrandService {

    @Resource
    private BrandMapper brandMapper;

    @Resource
    private CategoryBrandMapper categoryBrandMapper;

    @Override
    public Result<PageInfo<BrandEntity>> getBrandInfo(BrandDTO brandDTO) {

        //分页
        if(ObjectUtil.isNotNull(brandDTO.getPage()) && ObjectUtil.isNotNull(brandDTO.getRows()))
            PageHelper.startPage(brandDTO.getPage(),brandDTO.getRows());

        //排序
        Example example = new Example(BrandEntity.class);
        if(!StringUtil.isEmpty(brandDTO.getOrder()))example.setOrderByClause(brandDTO.getOrderByClause());

        if (ObjectUtil.isNotNull(brandDTO.getId())) example.createCriteria().andEqualTo("id",brandDTO.getId());

        //条件查询
        if(!StringUtil.isEmpty(brandDTO.getName())) example.createCriteria()
                .andLike("name","%" + brandDTO.getName() + "%");

        //数据封装
        PageInfo<BrandEntity> pageInfo = new PageInfo<>(brandMapper.selectByExample(example));

        return this.setResultSuccess(pageInfo);
    }

    @Transactional
    @Override
    public Result<JsonObject> saveBrand(BrandDTO brandDTO) {


        BrandEntity brandEntity = BaiDuBeanUtil.copyProperties(brandDTO, BrandEntity.class);
        //新增品牌返回主键
//        //获得品牌名称
//        String name = brandEntity.getName();
//        //获得品牌名称第一个字符
//        char c = name.charAt(0);
//        //将第一个字符转换为拼音
//        //获取拼音的首字母
//        //统一转为大写
//        String upperCase = PinYinUtil.getUpperCase(String.valueOf(c), PinYinUtil.TO_FIRST_CHAR_PINYIN);
//        brandEntity.setLetter(upperCase.charAt(0));
        brandEntity.setLetter(PinYinUtil.getUpperCase(String.valueOf(brandEntity.getName().charAt(0))
                , PinYinUtil.TO_FIRST_CHAR_PINYIN).charAt(0));

        brandMapper.insertSelective(brandEntity);

        //绑定关系
        if (StringUtil.isEmpty(brandDTO.getCategory())) return this.setResultError("品牌分类信息不能为空");

//        //判断品牌分类信息是否有多个
//        if (brandDTO.getCategory().contains(",")){
//
////            //分割数组
////            String[] split = brandDTO.getCategory().split(",");
////            //将Array转换为list
////            List<String> list = Arrays.asList(split);
////            ArrayList<CategoryBrandEntity> categoryBrandEntities = new ArrayList<>();
////
////            list.stream().forEach(cid ->{
////                CategoryBrandEntity entity = new CategoryBrandEntity();
////                entity.setCategoryId(StringUtil.toInteger(cid));
////                entity.setBrandId(brandEntity.getId());
////                categoryBrandEntities.add(entity);
////            });
//
//            //Arrays.asList将Array转换为List
//            //使用JDK1,8的stream
//            //使用map函数返回一个新的数据
//            List<CategoryBrandEntity> categoryBrandEntities = Arrays.asList(brandDTO.getCategory().split(","))
//                    .stream().map(cid ->{
//                CategoryBrandEntity entity = new CategoryBrandEntity();
//                entity.setCategoryId(StringUtil.toInteger(cid));
//                entity.setBrandId(brandEntity.getId());
//
//                return entity;
//            }).collect(Collectors.toList());
//
//            //批量新增
//            categoryBrandMapper.insertList(categoryBrandEntities);
//        }else{
//
//            //新增
//            CategoryBrandEntity categoryBrandEntity = new CategoryBrandEntity();
//            categoryBrandEntity.setCategoryId(StringUtil.toInteger(brandDTO.getCategory()));
//            categoryBrandEntity.setBrandId(brandEntity.getId());
//
//            categoryBrandMapper.insertSelective(categoryBrandEntity);
//        }
        this.insertCategoryBrand(brandDTO,brandEntity);

        return this.setResultSuccess();
    }

    @Override
    @Transactional
    public Result<JSONObject> update(BrandDTO brandDTO) {

        BrandEntity brandEntity = BaiDuBeanUtil.copyProperties(brandDTO, BrandEntity.class);
        brandEntity.setLetter(PinYinUtil.getUpperCase(String.valueOf(brandEntity.getName().charAt(0))
                , PinYinUtil.TO_FIRST_CHAR_PINYIN).charAt(0));
        //执行修改操作
        brandMapper.updateByPrimaryKeySelective(brandEntity);
        //删除关系表数据
        this.deleteCategoryBrand(brandEntity.getId());
        //增加新的数据
        this.insertCategoryBrand(brandDTO,brandEntity);

        return this.setResultSuccess();
    }

    @Transactional
    @Override
    public Result<JSONObject> deleteBrand(Integer id) {

        //删除品牌
        brandMapper.deleteByPrimaryKey(id);
        //删除关系
        this.deleteCategoryBrand(id);

        return this.setResultSuccess();
    }

    private void deleteCategoryBrand(Integer id){
        //通过brandId删除中间表的数据
        Example example = new Example(CategoryBrandEntity.class);
        example.createCriteria().andEqualTo("brandId",id);
        categoryBrandMapper.deleteByExample(example);
    }


    private void insertCategoryBrand(BrandDTO brandDTO,BrandEntity brandEntity){
        if (brandDTO.getCategory().contains(",")){
            List<CategoryBrandEntity> categoryBrandEntities = Arrays.asList(brandDTO.getCategory().split(","))
                    .stream().map(cid ->{
                        CategoryBrandEntity entity = new CategoryBrandEntity();
                        entity.setCategoryId(StringUtil.toInteger(cid));
                        entity.setBrandId(brandEntity.getId());

                        return entity;
                    }).collect(Collectors.toList());
            categoryBrandMapper.insertList(categoryBrandEntities);
        }else{
            CategoryBrandEntity categoryBrandEntity = new CategoryBrandEntity();
            categoryBrandEntity.setCategoryId(StringUtil.toInteger(brandDTO.getCategory()));
            categoryBrandEntity.setBrandId(brandEntity.getId());
            categoryBrandMapper.insertSelective(categoryBrandEntity);
        }
    }
}
