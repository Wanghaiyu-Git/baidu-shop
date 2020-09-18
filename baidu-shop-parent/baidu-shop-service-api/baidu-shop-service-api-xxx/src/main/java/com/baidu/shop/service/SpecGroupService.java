package com.baidu.shop.service;

import com.alibaba.fastjson.JSONObject;
import com.baidu.shop.base.Result;
import com.baidu.shop.dto.SpecGroupDTO;
import com.baidu.shop.dto.SpecParamDTO;
import com.baidu.shop.entity.SpecGroupEntity;
import com.baidu.shop.entity.SpecParamEntity;
import com.baidu.shop.validate.grop.BaiDuOperation;
import com.google.gson.JsonObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "规格接口")
public interface SpecGroupService {

    @ApiOperation(value = "通过条件查询规格组")
    @GetMapping(value = "specGrop/list")
    Result<List<SpecGroupEntity>> specGropList(SpecGroupDTO specGroupDTO);

    @ApiOperation(value = "增加规格组")
    @PostMapping(value = "specGrop/add")
    Result<JSONObject> specGroupAdd(@Validated({BaiDuOperation.add.class}) @RequestBody SpecGroupDTO specGroupDTO);

    @ApiOperation(value = "修改规格组")
    @PutMapping(value = "specGrop/add")
    Result<JSONObject> specGroupEdit(@Validated({BaiDuOperation.update.class})@RequestBody SpecGroupDTO specGroupDTO);

    @ApiOperation(value = "删除规格组")
    @DeleteMapping(value = "specGroup/delete")
    Result<JsonObject> specGroupDelete(Integer id);

    @ApiOperation(value = "通过条件查询规格参数")
    @GetMapping(value = "specParam/list")
    Result<List<SpecParamEntity>> specParamList(@SpringQueryMap SpecParamDTO specParamDTO);

    @ApiOperation(value = "增加规格参数")
    @PostMapping(value = "specParam/add")
    Result<JSONObject> specParamAdd(@Validated({BaiDuOperation.add.class}) @RequestBody SpecParamDTO specParamDTO);

    @ApiOperation(value = "修改规格参数")
    @PutMapping(value = "specParam/add")
    Result<JSONObject> specParamEdit(@Validated({BaiDuOperation.update.class}) @RequestBody SpecParamDTO specParamDTO);

    @ApiOperation(value = "删除规格参数")
    @DeleteMapping(value = "specParam/delete")
    Result<JSONObject> specParamDelete(Integer id);
}
