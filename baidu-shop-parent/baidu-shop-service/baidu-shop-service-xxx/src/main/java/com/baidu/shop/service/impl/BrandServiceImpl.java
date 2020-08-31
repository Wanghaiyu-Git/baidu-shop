package com.baidu.shop.service.impl;

import com.baidu.shop.base.BaseApiService;
import com.baidu.shop.base.Result;
import com.baidu.shop.dto.BrandDTO;
import com.baidu.shop.entity.BrandEntity;
import com.baidu.shop.mapper.BrandMapper;
import com.baidu.shop.service.BrandService;
import com.baidu.shop.utils.BaiDuBeanUtil;
import com.baidu.shop.utils.StringUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.gson.JsonObject;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.List;

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

    @Override
    public Result<PageInfo<BrandEntity>> getBrandInfo(BrandDTO brandDTO) {

        //分页
        PageHelper.startPage(brandDTO.getPage(),brandDTO.getRows());

        //排序
        Example example = new Example(BrandEntity.class);
        if(!StringUtil.isEmpty(brandDTO.getOrder()))example.setOrderByClause(brandDTO.getOrderByClause());

        //条件查询
        if(!StringUtil.isEmpty(brandDTO.getName())) example.createCriteria()
                .andLike("name","%" + brandDTO.getName() + "%");

        List<BrandEntity> list = brandMapper.selectByExample(example);

        //数据封装
        PageInfo<BrandEntity> pageInfo = new PageInfo<>(list);

        return this.setResultSuccess(pageInfo);
    }

    @Transactional
    @Override
    public Result<JsonObject> saveBrand(BrandDTO brandDTO) {

        brandMapper.insertSelective(BaiDuBeanUtil.copyProperties(brandDTO,BrandEntity.class));
        return this.setResultSuccess();
    }
}
