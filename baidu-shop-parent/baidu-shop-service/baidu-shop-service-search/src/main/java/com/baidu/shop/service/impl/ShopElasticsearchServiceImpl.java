package com.baidu.shop.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baidu.shop.base.BaseApiService;
import com.baidu.shop.base.Result;
import com.baidu.shop.document.GoodsDoc;
import com.baidu.shop.dto.SkuDTO;
import com.baidu.shop.dto.SpecGroupDTO;
import com.baidu.shop.dto.SpecParamDTO;
import com.baidu.shop.dto.SpuDTO;
import com.baidu.shop.entity.SpecParamEntity;
import com.baidu.shop.entity.SpuDepailEntity;
import com.baidu.shop.entity.SpuEntity;
import com.baidu.shop.feign.GoodsFeign;
import com.baidu.shop.feign.SpecGroupFeign;
import com.baidu.shop.service.ShopElasticsearchService;
import com.baidu.shop.status.HTTPStatus;
import com.baidu.shop.utils.JSONUtil;
import org.apache.commons.lang3.math.NumberUtils;
import com.github.pagehelper.PageInfo;
import com.netflix.discovery.converters.Auto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.IndexOperations;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName ShopElasticsearchServiceImpl
 * @Description: TODO
 * @Author wanghaiyu
 * @Date 2020/9/16
 * @Version V1.0
 **/
@RestController
public class ShopElasticsearchServiceImpl extends BaseApiService implements ShopElasticsearchService {

    @Autowired
    private GoodsFeign goodsFeign;

    @Autowired
    private SpecGroupFeign specGroupFeign;

    @Autowired
    private ElasticsearchRestTemplate elasticsearchRestTemplate;


    @Override
    public Result<JSONObject> clearEsData() {

        IndexOperations indexOperations = elasticsearchRestTemplate.indexOps(GoodsDoc.class);
        if (indexOperations.exists()) indexOperations.delete();
        System.out.println("索引删除成功");
        return this.setResultSuccess();
    }

    @Override
    public Result<JSONObject> initEsData() {
        IndexOperations indexOperations = elasticsearchRestTemplate.indexOps(GoodsDoc.class);
        if(!indexOperations.exists()){
            indexOperations.create();
            System.out.println("索引创建成功");
            indexOperations.createMapping();
            System.out.println("映射创建成功");
        }

        //批量新增数据
        List<GoodsDoc> goodsDocs = this.esGoodsInfo();
        elasticsearchRestTemplate.save(goodsDocs);
        return this.setResultSuccess();
    }


    private List<GoodsDoc> esGoodsInfo() {

        SpuDTO spuDTO = new SpuDTO();
        //查询出来的数据是多个spu
        List<GoodsDoc> goodsDocs = new ArrayList<>();
        Result<List<SpuDTO>> pageInfoResult = goodsFeign.spuList(spuDTO);

        if(pageInfoResult.getCode() == HTTPStatus.OK){

            //spu数据
            pageInfoResult.getData().stream().forEach(spu ->{

                GoodsDoc goodsDoc = new GoodsDoc();

                goodsDoc.setId(spu.getId().longValue());
                goodsDoc.setTitle(spu.getTitle());
                goodsDoc.setSubTitle(spu.getSubTitle());
                goodsDoc.setBrandName(spu.getBrandName());
                goodsDoc.setCategoryName(spu.getCategoryName());
                goodsDoc.setBrandId(spu.getBrandId().longValue());
                goodsDoc.setCid1(spu.getCid1().longValue());
                goodsDoc.setCid2(spu.getCid2().longValue());
                goodsDoc.setCid3(spu.getCid3().longValue());
                goodsDoc.setCreateTime(spu.getCreateTime());

                //通过spuId获取sku的数据  sku数据填充
                Map<List<Long>, List<Map<String, Object>>> skus = this.getSkusAndPriceList(spu.getId());
                skus.forEach((key, value) -> {
                    goodsDoc.setPrice(key);
                    goodsDoc.setSkus(JSONUtil.toJsonString(value));
                });
                //通过cid3查询规格参数
                Map<String, Object> specMap = this.getSpecMap(spu);
                //特有属性赋值
                goodsDoc.setSpecs(specMap);
                goodsDocs.add(goodsDoc);
            });
        }
        return goodsDocs;
    }

    //通过spuId获取sku的数据  sku数据填充
    private Map<List<Long>,List<Map<String,Object>>> getSkusAndPriceList(Integer spuId){

        HashMap<List<Long>, List<Map<String, Object>>> hashMap = new HashMap<>();

        Result<List<SkuDTO>> skuBySpuId = goodsFeign.getSkuBySpuId(spuId);
        ArrayList<Long> pricesList = new ArrayList<>();
        List<Map<String, Object>> skuMap = null;

        if(skuBySpuId.getCode() == HTTPStatus.OK){

            List<SkuDTO> skuDataList = skuBySpuId.getData();
            skuMap = skuDataList.stream().map(sku -> {
                Map<String, Object> map = new HashMap<>();

                map.put("id", sku.getId());
                map.put("title", sku.getTitle());
                map.put("images", sku.getImages());
                map.put("price", sku.getPrice());

                pricesList.add(sku.getPrice().longValue());
                return map;
            }).collect(Collectors.toList());
        }
        hashMap.put(pricesList,skuMap);
        return hashMap;
    }


    //通过cid3查询规格参数
    private Map<String,Object> getSpecMap(SpuDTO spuDTO){
        SpecParamDTO specParamDTO = new SpecParamDTO();
        specParamDTO.setCid(spuDTO.getCid3());
        Result<List<SpecParamEntity>> specParamList = specGroupFeign.specParamList(specParamDTO);
        Map<String, Object> specMap = new HashMap<>();
        if(specParamList.getCode() == HTTPStatus.OK){

            //规格参数
            List<SpecParamEntity> paramList = specParamList.getData();
            //通过spuId获得Detail数据,detail里面有通用或特殊规格参数的值
            Result<SpuDepailEntity> spuDepailBySpu = goodsFeign.getSpuDepailBySpu(spuDTO.getId());
            if(spuDepailBySpu.getCode() == HTTPStatus.OK){
                SpuDepailEntity spuDepailInfo = spuDepailBySpu.getData();

                //通用参数的值  转为Map
                String genericSpecStr = spuDepailInfo.getGenericSpec();
                Map<String, String> genericSpecMap = JSONUtil.toMapValueString(genericSpecStr);

                //特有参数的值  转为Map,值为List,因为值可能会有多个
                String specialSpecStr = spuDepailInfo.getSpecialSpec();
                Map<String, List<String>> specialSpecMap = JSONUtil.toMapValueStrList(specialSpecStr);

                paramList.stream().forEach(param -> {

                    if (param.getGeneric()){
                        if (param.getNumeric() && param.getSearching()){
                            specMap.put(param.getName(),this.chooseSegment(genericSpecMap.get(param.getId() + ""),param.getSegments(),param.getUnit()));
                        }else{
                            specMap.put(param.getName(),genericSpecMap.get(param.getId() + ""));
                        }
                    }else{
                        //特殊规格
                        specMap.put(param.getName(),specialSpecMap.get(param.getId() + ""));
                    }
                });
            }
        }
        return specMap;
    }

    /**
     * 把具体的值转换成区间-->不做范围查询
     * @param value
     * @param segments
     * @param unit
     * @return
     */
    private String chooseSegment(String value, String segments, String unit) {
        double val = NumberUtils.toDouble(value);
        String result = "其它";
        // 保存数值段
        for (String segment : segments.split(",")) {
            String[] segs = segment.split("-");
            // 获取数值范围
            double begin = NumberUtils.toDouble(segs[0]);
            double end = Double.MAX_VALUE;
            if(segs.length == 2){
                end = NumberUtils.toDouble(segs[1]);
            }
            // 判断是否在范围内
            if(val >= begin && val < end){
                if(segs.length == 1){
                    result = segs[0] + unit + "以上";
                }else if(begin == 0){
                    result = segs[1] + unit + "以下";
                }else{
                    result = segment + unit;
                }
                break;
            }
        }
        return result;
    }

}
