package com.baidu.shop.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baidu.shop.base.Result;
import com.baidu.shop.dto.BrandDTO;
import com.baidu.shop.entity.BrandEntity;
import com.baidu.shop.validate.grop.BaiDuOperation;
import com.github.pagehelper.PageInfo;
import com.google.gson.JsonObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Api(tags = "品牌管理接口")
public interface BrandService {

    @ApiOperation(value = "通过分类id获取品牌")
    @GetMapping(value = "brand/getBrandByCategory")
    Result<List<BrandEntity>> getBrandByCategory(Integer cid);

    @ApiOperation(value = "获取品牌信息")
    @GetMapping(value = "brand/getBrandInfo")
    Result<PageInfo<BrandEntity>> getBrandInfo(BrandDTO brandDTO);

    @ApiOperation(value = "增加品牌信息")
    @PostMapping(value = "brand/save")
    Result<JsonObject> saveBrand(@Validated({BaiDuOperation.add.class}) @RequestBody BrandDTO brandDTO);

    @ApiOperation(value = "修改品牌信息")
    @PutMapping(value = "brand/update")
    Result<JSONObject> update(@Validated({BaiDuOperation.update.class}) @RequestBody BrandDTO brandDTO);

    @ApiOperation(value = "删除品牌信息")
    @DeleteMapping(value = "brand/delete")
    Result<JSONObject> deleteBrand(Integer id);
}
