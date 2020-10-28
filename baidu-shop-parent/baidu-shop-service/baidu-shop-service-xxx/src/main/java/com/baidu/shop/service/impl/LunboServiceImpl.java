package com.baidu.shop.service.impl;

import com.baidu.shop.base.BaseApiService;
import com.baidu.shop.base.Result;
import com.baidu.shop.entity.BrandEntity;
import com.baidu.shop.entity.LunboEntity;
import com.baidu.shop.mapper.LunboMapper;
import com.baidu.shop.service.LunboService;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.spring.web.json.Json;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @ClassName LunboServiceImpl
 * @Description: TODO
 * @Author wanghaiyu
 * @Date 2020/10/27
 * @Version V1.0
 **/
@RestController
public class LunboServiceImpl extends BaseApiService implements LunboService {

    @Resource
    private LunboMapper lunboMapper;

    @Override
    public Result<JSONObject> gundongtuInfo() {
        List<LunboEntity> lunboEntities = lunboMapper.selectAll();
        return this.setResultSuccess(lunboEntities);
    }

    @Override
    public Result<JSONObject> addGundongtu(LunboEntity lunboEntity) {
        LunboEntity entity = new LunboEntity();
        entity.setImage(lunboEntity.getImage());
        entity.setTiaozhuan(lunboEntity.getTiaozhuan());
        lunboMapper.insertSelective(entity);
        return this.setResultSuccess();
    }

    @Override
    public Result<JSONObject> deleteGundongtu(Integer id) {
        lunboMapper.deleteByPrimaryKey(id);
        return this.setResultSuccess();
    }

    @Override
    public Result<LunboEntity> getLunboById(Integer lunboId) {
        LunboEntity entity = lunboMapper.selectByPrimaryKey(lunboId);
        return this.setResultSuccess(entity);
    }

    @Override
    public Result<JSONObject> updateGundongtu(LunboEntity lunboEntity) {
        lunboMapper.updateByPrimaryKeySelective(lunboEntity);
        return this.setResultSuccess();
    }
}
