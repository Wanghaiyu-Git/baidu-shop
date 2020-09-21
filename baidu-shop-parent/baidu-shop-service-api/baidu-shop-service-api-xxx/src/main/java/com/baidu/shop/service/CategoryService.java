package com.baidu.shop.service;

import com.alibaba.fastjson.JSONObject;
import com.baidu.shop.base.Result;
import com.baidu.shop.entity.CategoryEntity;
import com.baidu.shop.validate.grop.BaiDuOperation;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "商品分类接口")
public interface CategoryService {

    @ApiOperation(value = "通过查询商品分类")
    @GetMapping(value = "category/list")
    Result<List<CategoryEntity>> getCategoryByPid(Integer pid);

    @ApiOperation(value = "通过品牌id查询商品分类")
    @GetMapping(value = "category/getBrand")
    Result<List<CategoryEntity>> getBybrand(Integer brandId);

    @ApiOperation(value = "新增分类")
    @PostMapping(value = "category/save")
    Result<JSONObject> saveCategory(@Validated({BaiDuOperation.add.class}) @RequestBody CategoryEntity categoryEntity);

    @ApiOperation(value = "修改分类")
    @PutMapping(value = "category/edit")
    Result<JSONObject> editCategory(@Validated({BaiDuOperation.update.class}) @RequestBody CategoryEntity categoryEntity);

    @ApiOperation(value = "删除分类")
    @DeleteMapping(value = "category/delete")
    Result<JSONObject> deleteCategory(Integer id);

    @ApiOperation(value = "通过分类id集合查询分类数据")
    @GetMapping(value = "get/categoryByIdList")
    Result<List<CategoryEntity>> getCategoryByIdList(@RequestParam String cidsStr);
}

