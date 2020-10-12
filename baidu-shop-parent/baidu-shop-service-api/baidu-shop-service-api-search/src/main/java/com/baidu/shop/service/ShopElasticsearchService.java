package com.baidu.shop.service;

import com.alibaba.fastjson.JSONObject;
import com.baidu.shop.base.Result;
import com.baidu.shop.document.GoodsDoc;
import com.baidu.shop.response.GoodsResponse;
import com.google.gson.JsonObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @ClassName ShopElasticsearchService
 * @Description: TODO
 * @Author wanghaiyu
 * @Date 2020/9/16
 * @Version V1.0
 **/
@Api(tags = "ES接口")
public interface ShopElasticsearchService {

    @ApiOperation(value = "清空ES中的商品数据")
    @GetMapping(value = "es/clearGoodsEsData")
    Result<JSONObject> clearEsData();

    @ApiOperation(value = "初始化es数据")
    @GetMapping(value = "es/initEsData")
    Result<JSONObject> initEsData();

    @ApiOperation(value = "查询es数据")
    @GetMapping(value = "es/search")
    GoodsResponse search(@RequestParam String search, @RequestParam Integer page,@RequestParam String filter);

    @ApiOperation(value = "新增数据到es")
    @PostMapping(value = "es/saveData")
    Result<JsonObject> saveData(Integer spuId);

    @ApiOperation(value = "通过id删除es数据")
    @DeleteMapping(value = "es/deleteData")
    Result<JsonObject> deleteData(Integer spuId);
}
