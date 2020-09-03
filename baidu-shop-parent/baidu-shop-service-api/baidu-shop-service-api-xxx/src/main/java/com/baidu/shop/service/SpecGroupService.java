package com.baidu.shop.service;

import com.alibaba.fastjson.JSONObject;
import com.baidu.shop.base.Result;
import com.baidu.shop.dto.SpecGroupDTO;
import com.baidu.shop.entity.SpecGroupEntity;
import com.baidu.shop.utils.BaiDuBeanUtil;
import com.baidu.shop.validate.grop.BaiDuOperation;
import com.google.gson.JsonObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "规格接口")
public interface SpecGroupService {

    @ApiOperation(value = "通过条件查询规格参数组")
    @GetMapping(value = "specGrop/list")
    Result<List<SpecGroupEntity>> specGropList(SpecGroupDTO specGroupDTO);

    @ApiOperation(value = "增加规格参数组")
    @PostMapping(value = "specGrop/add")
    Result<JSONObject> specGroupAdd(@Validated({BaiDuOperation.add.class}) @RequestBody SpecGroupDTO specGroupDTO);

    @ApiOperation(value = "修改规格参数组")
    @PutMapping(value = "specGrop/add")
    Result<JSONObject> specGroupEdit(@Validated({BaiDuOperation.update.class})@RequestBody SpecGroupDTO specGroupDTO);

    @ApiOperation(value = "删除规格参数组")
    @DeleteMapping(value = "specGroup/delete")
    Result<JsonObject> specGroupDelete(Integer id);
}
