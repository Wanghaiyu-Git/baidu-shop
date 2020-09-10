package com.baidu.shop.service;

import com.alibaba.fastjson.JSONObject;
import com.baidu.shop.base.Result;
import com.baidu.shop.dto.SkuDTO;
import com.baidu.shop.dto.SpuDTO;
import com.baidu.shop.entity.SpuDepailEntity;
import com.baidu.shop.entity.SpuEntity;
import com.github.pagehelper.PageInfo;
import com.google.gson.JsonObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "商品接口")
public interface GoodsService {

    @ApiOperation(value = "获取spu信息")
    @GetMapping(value = "goods/spuList")
    Result<PageInfo<SpuEntity>> spuList(SpuDTO spuDTO);

    @ApiOperation(value = "保存商品信息")
    @PostMapping(value = "goods/goodsSave")
    Result<JsonObject> goodsSave(@RequestBody SpuDTO spuDTO);

    @ApiOperation(value = "通过spuId获取depail信息")
    @GetMapping(value = "goods/getSpuDepailBySpu")
    Result<SpuDepailEntity> getSpuDepailBySpu(Integer spuId);

    @ApiOperation(value = "通过spuId获取sku信息")
    @GetMapping(value = "goods/getSkuBySpuId")
    Result<List<SkuDTO>> getSkuBySpuId(Integer spuId);

    @ApiOperation(value = "修改商品信息")
    @PutMapping(value = "goods/goodsSave")
    Result<JsonObject> goodsEdit(@RequestBody SpuDTO spuDTO);

    @ApiOperation(value = "删除商品")
    @DeleteMapping(value = "goods/delete")
    Result<JSONObject> goodsDelete(Integer spuId);
}
