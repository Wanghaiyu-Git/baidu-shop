package com.baidu.shop.mapper;

import com.baidu.shop.entity.CategoryBrandEntity;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.special.InsertListMapper;

/**
 * @ClassName CategoryBrandMapper
 * @Description: TODO
 * @Author wanghaiyu
 * @Date 2020/9/1
 * @Version V1.0
 **/
//接口多继承InsertListMapper用于新增
public interface CategoryBrandMapper extends InsertListMapper<CategoryBrandEntity>, Mapper<CategoryBrandEntity> {
}
