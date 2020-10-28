package com.baidu.shop.service;

import com.baidu.shop.base.Result;
import com.baidu.shop.dto.LunbotuDTO;
import com.baidu.shop.entity.BrandEntity;
import com.baidu.shop.entity.LunboEntity;
import com.baidu.shop.validate.grop.BaiDuOperation;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.json.JSONObject;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.spring.web.json.Json;

import java.util.List;

/**
 * @ClassName LunboService
 * @Description: TODO
 * @Author wanghaiyu
 * @Date 2020/10/27
 * @Version V1.0
 **/
@Api(tags = "轮播图接口")
public interface LunboService {

    @ApiOperation(value = "查询数据")
    @GetMapping(value = "gundongtu/gundongtuInfo")
    Result<JSONObject> gundongtuInfo();

    @ApiOperation(value = "新增数据")
    @PostMapping(value = "gundongtu/addGundongtu")
    Result<JSONObject> addGundongtu(@RequestBody LunboEntity lunboEntity);

    @ApiOperation(value = "删除数据")
    @DeleteMapping(value = "gundongtu/deleteGundongtu")
    Result<JSONObject> deleteGundongtu(Integer id);

    @ApiOperation(value = "通过轮播图id集合获取轮播图信息")
    @GetMapping(value = "get/lunboById")
    Result<LunboEntity> getLunboById(Integer lunboId);

    @ApiOperation(value = "修改数据")
    @PutMapping(value = "gundongtu/updateGundongtu")
    Result<JSONObject> updateGundongtu(@RequestBody LunboEntity lunboEntity);

}
