package com.baidu.shop.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baidu.shop.base.BaseApiService;
import com.baidu.shop.base.Result;
import com.baidu.shop.document.GoodsDoc;
import com.baidu.shop.dto.SkuDTO;
import com.baidu.shop.dto.SpecGroupDTO;
import com.baidu.shop.dto.SpecParamDTO;
import com.baidu.shop.dto.SpuDTO;
import com.baidu.shop.entity.*;
import com.baidu.shop.feign.BrandFeign;
import com.baidu.shop.feign.CategoryFeign;
import com.baidu.shop.feign.GoodsFeign;
import com.baidu.shop.feign.SpecGroupFeign;
import com.baidu.shop.response.GoodsResponse;
import com.baidu.shop.service.ShopElasticsearchService;
import com.baidu.shop.status.HTTPStatus;
import com.baidu.shop.utils.ESHighLightUtil;
import com.baidu.shop.utils.JSONUtil;
import com.baidu.shop.utils.StringUtil;
import com.google.common.math.DoubleMath;
import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.math.NumberUtils;
import com.github.pagehelper.PageInfo;
import com.netflix.discovery.converters.Auto;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.IndexOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.FetchSourceFilter;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.web.bind.EscapedErrors;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @ClassName ShopElasticsearchServiceImpl
 * @Description: TODO
 * @Author wanghaiyu
 * @Date 2020/9/16
 * @Version V1.0
 **/
@RestController
@Slf4j
public class ShopElasticsearchServiceImpl extends BaseApiService implements ShopElasticsearchService {

    @Autowired
    private GoodsFeign goodsFeign;

    @Autowired
    private SpecGroupFeign specGroupFeign;

    @Autowired
    private ElasticsearchRestTemplate elasticsearchRestTemplate;

    @Autowired
    private CategoryFeign categoryFeign;

    @Autowired
    private BrandFeign brandFeign;

    @Override
    public Result<JsonObject> saveData(Integer spuId) {
        //通过spuId查询数据
        SpuDTO spuDTO = new SpuDTO();
        spuDTO.setId(spuId);
        List<GoodsDoc> goodsDocs = this.esGoodsInfo(spuDTO);
        elasticsearchRestTemplate.save(goodsDocs.get(0));
        return this.setResultSuccess();
    }

    @Override
    public Result<JsonObject> deleteData(Integer spuId) {

        GoodsDoc goodsDoc = new GoodsDoc();
        goodsDoc.setId(spuId.longValue());
        elasticsearchRestTemplate.delete(goodsDoc);

        return this.setResultSuccess();
    }

    /**
     * ES搜索
     * @param search
     * @param page
     * @return
     */
    @Override
    public GoodsResponse search(String search,Integer page,String filter) {
        if(StringUtil.isEmpty(search)) throw new RuntimeException("查询内容不能为空");//判断搜索内容不能为空

        SearchHits<GoodsDoc> searchHits = elasticsearchRestTemplate.search(this.getSearchQueryBuilder(search, page, filter).build(), GoodsDoc.class);
        List<SearchHit<GoodsDoc>> highLightHit = ESHighLightUtil.getHighLightHit(searchHits.getSearchHits());

        List<GoodsDoc> goodsList = highLightHit.stream().map(searchHit -> searchHit.getContent()).collect(Collectors.toList());//返回的商品内容

        long total = searchHits.getTotalHits();//总条数
        long totalPage = Double.valueOf(Math.ceil(Long.valueOf(total).doubleValue() / 10)).longValue();//总页数

        Aggregations aggregations = searchHits.getAggregations();
        List<BrandEntity> brandList = this.getBrandList(aggregations);//获取品牌集合

//        List<CategoryEntity> categoryList = this.getCategoryList(aggregations);//获取分类集合
        Map<Integer, List<CategoryEntity>> map = this.getCategoryList(aggregations);

        List<CategoryEntity> categoryList = null;
        Integer hotCid = 0;

        for (Map.Entry<Integer,List<CategoryEntity>> mapEntry : map.entrySet()) {
            hotCid = mapEntry.getKey();
            categoryList = mapEntry.getValue();
        }

        Map<String, List<String>> specParamValueMap = this.getSpecParam(hotCid, search);
        return new GoodsResponse(total, totalPage, brandList, categoryList, goodsList,specParamValueMap);
    }

    /**
     * 清空es数据
     * @return
     */
    @Override
    public Result<JSONObject> clearEsData() {
        IndexOperations indexOperations = elasticsearchRestTemplate.indexOps(GoodsDoc.class);
        if (indexOperations.exists()) indexOperations.delete();
        return this.setResultSuccess();
    }

    /**
     * 初始化es数据
     * @return
     */
    @Override
    public Result<JSONObject> initEsData() {
        IndexOperations indexOperations = elasticsearchRestTemplate.indexOps(GoodsDoc.class);
        if(!indexOperations.exists()){
            indexOperations.create();
            indexOperations.createMapping();
        }
        //批量新增数据
        List<GoodsDoc> goodsDocs = this.esGoodsInfo(new SpuDTO());
        elasticsearchRestTemplate.save(goodsDocs);

        return this.setResultSuccess();
    }

    /**
     * 构建查询条件
     * @param search
     * @param page
     * @param filter
     * @return
     */
    private NativeSearchQueryBuilder getSearchQueryBuilder(String search, Integer page, String filter){
        NativeSearchQueryBuilder searchQueryBuilder = new NativeSearchQueryBuilder();

        if (StringUtil.isNotEmpty(filter) && filter.length() > 2) {
            BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
            Map<String, String> filterMap = JSONUtil.toMapValueString(filter);

            filterMap.forEach((key, value) -> {
                MatchQueryBuilder matchQueryBuilder = null;

                if (key.equals("cid3") || key.equals("brandId")){
                    matchQueryBuilder = QueryBuilders.matchQuery(key, value);
                }else{
                    matchQueryBuilder = QueryBuilders.matchQuery("specs." + key + ".keyword",value);
                }
                boolQueryBuilder.must(matchQueryBuilder);
            });
            searchQueryBuilder.withFilter(boolQueryBuilder);
        }

        searchQueryBuilder.withQuery(QueryBuilders.multiMatchQuery(search,"brandName","categoryName","title"));

        searchQueryBuilder.addAggregation(AggregationBuilders.terms("cid_agg").field("cid3"));//搜索过滤
        searchQueryBuilder.addAggregation(AggregationBuilders.terms("brand_agg").field("brandId"));

        searchQueryBuilder.withPageable(PageRequest.of(page-1,10));//分页

        searchQueryBuilder.withHighlightBuilder(ESHighLightUtil.getHighlightBuilder("title"));//设置高亮
        log.debug(search);
        return searchQueryBuilder;
    }

    /**
     * 获取分类集合
     * @param aggregations
     * @return
     */
    private Map<Integer,List<CategoryEntity>> getCategoryList(Aggregations aggregations){

        //搜索过滤
        Terms cid_agg = aggregations.get("cid_agg");

        //热度最高的cid
        List<Integer> hotCid = Arrays.asList(0);
        List<Long> maxCount = Arrays.asList(0L);
        Map<Integer, List<CategoryEntity>> map = new HashMap<>();


        //通过id集合查询数据
        List<String> cidList = cid_agg.getBuckets().stream().map(cidbucket -> {
            Number keyAsNumber = cidbucket.getKeyAsNumber();

            if (cidbucket.getDocCount() > maxCount.get(0)){
                maxCount.set(0,cidbucket.getDocCount());
                hotCid.set(0,keyAsNumber.intValue());
            }
            return keyAsNumber.intValue() + "";
        }).collect(Collectors.toList());

        //通过分类id集合查询数据
        Result<List<CategoryEntity>> categoryResult = categoryFeign.getCategoryByIdList(String.join(",", cidList));

        map.put(hotCid.get(0),categoryResult.getData());

        return map;
    }

    /**
     * 获取品牌集合
     * @param aggregations
     * @return
     */
    private List<BrandEntity> getBrandList(Aggregations aggregations){

        //搜索过滤
        Terms brand_agg = aggregations.get("brand_agg");
        List<? extends Terms.Bucket> brandBuckets = brand_agg.getBuckets();

        //通过id集合查询数据
        List<String> brandIdList = brandBuckets.stream()
                .map(brandBucket -> brandBucket.getKeyAsNumber().intValue() + "")
                .collect(Collectors.toList());

        //通过品牌id集合查询数据
        Result<List<BrandEntity>> brandResult = brandFeign.getBrandByIdList(String.join(",", brandIdList));
        return brandResult.getData();
    }

    /**
     * 通过cid查询规格参数
     * @param hotCid
     */
    private Map<String, List<String>> getSpecParam(Integer hotCid,String search){

        SpecParamDTO specParamDTO = new SpecParamDTO();
        specParamDTO.setCid(hotCid);
        specParamDTO.setSearching(true);
        Result<List<SpecParamEntity>> specParamResult = specGroupFeign.specParamList(specParamDTO);

        if (specParamResult.getCode() == 200) {

            List<SpecParamEntity> speaParamList = specParamResult.getData();
            //聚合查询
            NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
            queryBuilder.withQuery(QueryBuilders.multiMatchQuery(search,"brandName","categoryName","title"));
            queryBuilder.withPageable(PageRequest.of(0,1));//分页

            speaParamList.stream().forEach(specParam -> {

                queryBuilder.addAggregation(AggregationBuilders.terms(specParam.getName()).field("specs."+specParam.getName()+".keyword"));

            });

            SearchHits<GoodsDoc> searchHits = elasticsearchRestTemplate.search(queryBuilder.build(), GoodsDoc.class);

            //key=paramName
            Map<String, List<String>> map = new HashMap<>();
            Aggregations aggregations = searchHits.getAggregations();

            speaParamList.stream().forEach(specParam -> {
                Terms terms = aggregations.get(specParam.getName());

                List<? extends Terms.Bucket> buckets = terms.getBuckets();
                List<String> valueList = buckets.stream().map(bucket -> bucket.getKeyAsString()).collect(Collectors.toList());

                map.put(specParam.getName(),valueList);
            });

            return map;
        }
        log.debug(search);
        return null;
    }


    /**
     * 获取mysql数据
     * @return
     */
    private List<GoodsDoc> esGoodsInfo(SpuDTO spuDTO) {

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

    /**
     * 通过cid3查询规格参数
     * @param spuDTO
     * @return
     */
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
