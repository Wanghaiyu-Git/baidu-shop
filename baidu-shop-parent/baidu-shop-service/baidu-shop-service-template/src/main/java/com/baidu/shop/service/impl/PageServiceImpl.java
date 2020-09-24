package com.baidu.shop.service.impl;

import com.baidu.shop.base.Result;
import com.baidu.shop.dto.*;
import com.baidu.shop.entity.*;
import com.baidu.shop.feign.BrandFeign;
import com.baidu.shop.feign.CategoryFeign;
import com.baidu.shop.feign.GoodsFeign;
import com.baidu.shop.feign.SpecGroupFeign;
import com.baidu.shop.service.PageService;
import com.baidu.shop.utils.BaiDuBeanUtil;
import com.github.pagehelper.PageInfo;
import com.netflix.discovery.converters.Auto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName PageServiceImpl
 * @Description: TODO
 * @Author wanghaiyu
 * @Date 2020/9/23
 * @Version V1.0
 **/
@Service
public class PageServiceImpl implements PageService {

    @Autowired
    private GoodsFeign goodsFeign;

    @Autowired
    private BrandFeign brandFeign;

    @Autowired
    private SpecGroupFeign specGroupFeign;

    @Autowired
    private CategoryFeign categoryFeign;

    @Override
    public Map<String, Object> getPageInfoBySpuId(Integer spuId) {

        Map<String, Object> map = new HashMap<>();

        SpuDTO spuDTO = new SpuDTO();
        spuDTO.setId(spuId);
        Result<List<SpuDTO>> listResult = goodsFeign.spuList(spuDTO);

        if (listResult.getCode() == 200) {

            if (listResult.getData().size() == 1) {
                //spu信息
                SpuDTO spuInfo = listResult.getData().get(0);
                map.put("spuInfo",spuInfo);
                //品牌信息
                BrandDTO brandDTO = new BrandDTO();
                brandDTO.setId(spuInfo.getBrandId());
                Result<PageInfo<BrandEntity>> brandInfo = brandFeign.getBrandInfo(brandDTO);

                if (brandInfo.getCode() == 200) {
                    PageInfo<BrandEntity> data = brandInfo.getData();
                    List<BrandEntity> brandList = data.getList();
                    if (brandList.size() == 1) {
                        map.put("brandInfo",brandList.get(0));
                    }
                }

                //分类信息
                Result<List<CategoryEntity>> categoryList = categoryFeign.getCategoryByIdList(String.join(",", Arrays
                        .asList(spuInfo.getCid1() + "", spuInfo.getCid2() + "", spuInfo.getCid3() + "")));

                if (categoryList.getCode() == 200) {
                    map.put("categoryList",categoryList.getData());
                }

                //detail信息
                Result<SpuDepailEntity> depailResult = goodsFeign.getSpuDepailBySpu(spuId);
                if (depailResult.getCode() == 200) {
                    SpuDepailEntity detailList = depailResult.getData();
                    map.put("detailList",detailList);
                }

                //sku信息
                Result<List<SkuDTO>> skuResult = goodsFeign.getSkuBySpuId(spuId);
                if (skuResult.getCode() == 200) {
                    List<SkuDTO> skuList = skuResult.getData();
                    map.put("skuList",skuList);
                }

                //规格组参数组
                SpecGroupDTO specGroupDTO = new SpecGroupDTO();
                specGroupDTO.setCid(spuId);
                Result<List<SpecGroupEntity>> specGropResult = specGroupFeign.specGropList(specGroupDTO);

                if (specGropResult.getCode() == 200) {
                    List<SpecGroupEntity> specGroupList = specGropResult.getData();
                    List<SpecGroupEntity> groupParamList = specGroupList.stream().map(specGroup -> {

                        SpecGroupEntity specGroupEntity = BaiDuBeanUtil.copyProperties(specGroup, SpecGroupEntity.class);

                        SpecParamDTO specParamDTO = new SpecParamDTO();
                        specParamDTO.setGroupId(specGroup.getId());
                        specParamDTO.setGeneric(true);
                        Result<List<SpecParamEntity>> specParamResult = specGroupFeign.specParamList(specParamDTO);
                        if (specParamResult.getCode() == 200) {
                            List<SpecParamEntity> specParamList = specParamResult.getData();
                            map.put("specParamList", specParamList);
                        }
                        return specGroupEntity;
                    }).collect(Collectors.toList());
                    map.put("groupParamList",groupParamList);
                }

                //特有参数规格
                SpecParamDTO specParamDTO = new SpecParamDTO();
                specParamDTO.setCid(spuDTO.getCid3());
                specParamDTO.setGeneric(false);
                Result<List<SpecParamEntity>> specParamResult = specGroupFeign.specParamList(specParamDTO);
                if (specParamResult.getCode() == 200) {
                    Map<Integer, String> specMap = new HashMap<>();

                    specParamResult.getData().stream().forEach(spec -> specMap.put(spec.getId(),spec.getName()));
                    map.put("specMap",specMap);
                }
            }
        }
        return map;
    }
}
