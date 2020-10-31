package com.baidu.shop.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baidu.shop.base.BaseApiService;
import com.baidu.shop.base.Result;
import com.baidu.shop.dto.*;
import com.baidu.shop.entity.*;
import com.baidu.shop.feign.BrandFeign;
import com.baidu.shop.feign.CategoryFeign;
import com.baidu.shop.feign.GoodsFeign;
import com.baidu.shop.feign.SpecGroupFeign;
import com.baidu.shop.service.TemplateService;
import com.baidu.shop.utils.BaiDuBeanUtil;
import com.github.pagehelper.PageInfo;
import com.netflix.discovery.converters.Auto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RestController;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName TemplateServiceImpl
 * @Description: TODO
 * @Author wanghaiyu
 * @Date 2020/9/25
 * @Version V1.0
 **/
@RestController
public class TemplateServiceImpl extends BaseApiService implements TemplateService {

    @Autowired
    private GoodsFeign goodsFeign;

    @Autowired
    private BrandFeign brandFeign;

    @Autowired
    private SpecGroupFeign specGroupFeign;

    @Autowired
    private CategoryFeign categoryFeign;

    @Value(value = "${mrshop.static.html.path}")
    private String staticHTMLPath;

    @Autowired
    private TemplateEngine templateEngine;

    @Override
    public Result<JSONObject> deleteHTMLBySpuId(Integer spuId) {
        File file = new File(staticHTMLPath + File.separator + spuId + ".html");
        if (file.delete()) {
            return this.setResultError("文件删除失败");
        }
        return this.setResultSuccess();
    }

    @Override
    public Result<JSONObject> createStaticHTMLTemplate(Integer spuId) {

        Map<String, Object> pageInfoBySpuId = this.getPageInfoBySpuId(spuId);

        //创建模板引擎上下文
        Context context = new Context();
        //将所有准备的数据放到模板中
        context.setVariables(pageInfoBySpuId);

        //创建文件 param1:文件路径 param2:文件名称
        File file = new File(staticHTMLPath, spuId + ".html");
        //构建文件输出流
        PrintWriter writer = null;
        try {
            writer = new PrintWriter(file, "UTF-8");

            //根据模板生成静态文件
            //param1:模板名称 params2:模板上下文[上下文中包含了需要填充的数据],文件输出
            templateEngine.process("item1",context,writer);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } finally {
            writer.close();
        }
        return this.setResultSuccess();
    }

    @Override
    public Result<JSONObject> initStaticHTMLTemplate() {

        Result<List<SpuDTO>> spuListResult = goodsFeign.spuList(new SpuDTO());
        if (spuListResult.getCode() == 200) {
            List<SpuDTO> spuDTOList = spuListResult.getData();
            spuDTOList.stream().forEach(spuDTO -> {
                createStaticHTMLTemplate(spuDTO.getId());
            });
        }
        return this.setResultSuccess();
    }


    private Map<String, Object> getPageInfoBySpuId(Integer spuId) {

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

                //特有参数规格
                SpecParamDTO specParamDTO = new SpecParamDTO();
                specParamDTO.setCid(spuInfo.getCid3());
                specParamDTO.setGeneric(false);
                Result<List<SpecParamEntity>> specParamResult = specGroupFeign.specParamList(specParamDTO);
                if (specParamResult.getCode() == 200) {
                    Map<Integer, String> specMap = new HashMap<>();

                    specParamResult.getData().stream().forEach(spec -> specMap.put(spec.getId(),spec.getName()));
                    map.put("specMap",specMap);
                }

                //规格组参数组
                SpecGroupDTO specGroupDTO = new SpecGroupDTO();
                specGroupDTO.setCid(spuInfo.getCid3());
                Result<List<SpecGroupEntity>> specGropListResult = specGroupFeign.specGropList(specGroupDTO);
                if (specGropListResult.getCode() == 200) {
                    List<SpecGroupEntity> specGroupData = specGropListResult.getData();

                    List<SpecGroupDTO> specGroupDTOList = specGroupData.stream().map(specGroupEntity -> {

                        SpecGroupDTO specGroup = BaiDuBeanUtil.copyProperties(specGroupEntity, SpecGroupDTO.class);

                        SpecParamDTO paramDTO = new SpecParamDTO();
                        paramDTO.setGroupId(specGroupEntity.getId());
                        paramDTO.setGeneric(true);

                        Result<List<SpecParamEntity>> specParamListResult = specGroupFeign.specParamList(paramDTO);
                        if (specParamListResult.getCode() == 200) {
                            specGroup.setParamList(specParamListResult.getData());
                        }
                        return specGroup;
                    }).collect(Collectors.toList());

                    map.put("specGroupDTOList",specGroupDTOList);
                }
            }
        }
        return map;
    }
}
