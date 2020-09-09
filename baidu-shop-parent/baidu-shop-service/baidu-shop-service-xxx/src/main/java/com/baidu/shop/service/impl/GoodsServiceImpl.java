package com.baidu.shop.service.impl;

import com.baidu.shop.base.BaseApiService;
import com.baidu.shop.base.Result;
import com.baidu.shop.dto.BrandDTO;
import com.baidu.shop.dto.SkuDTO;
import com.baidu.shop.dto.SpuDTO;
import com.baidu.shop.entity.*;
import com.baidu.shop.mapper.*;
import com.baidu.shop.service.BrandService;
import com.baidu.shop.service.GoodsService;
import com.baidu.shop.status.HTTPStatus;
import com.baidu.shop.utils.BaiDuBeanUtil;
import com.baidu.shop.utils.ObjectUtil;
import com.baidu.shop.utils.StringUtil;
import com.baidu.shop.validate.grop.BaiDuOperation;
import com.github.pagehelper.IPage;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @ClassName GoodsServiceImpl
 * @Description: TODO
 * @Author wanghaiyu
 * @Date 2020/9/7
 * @Version V1.0
 **/
@RestController
public class GoodsServiceImpl extends BaseApiService implements GoodsService {

    @Resource
    private GoodsMapper goodsMapper;

    @Resource
    private CategoryMapper categoryMapper;

    @Autowired
    private BrandService brandService;

    @Resource
    private SpuDepailMapper spuDepailMapper;

    @Resource
    private StockMapper stockMapper;

    @Resource
    private SkuMapper skuMapper;


    @Override
    public Result<PageInfo<SpuEntity>> spuList(SpuDTO spuDTO) {

        //分页
        if(ObjectUtil.isNotNull(spuDTO.getPage()) && ObjectUtil.isNotNull(spuDTO.getRows()))
            PageHelper.startPage(spuDTO.getPage(),spuDTO.getRows());

        //排序
        Example example = new Example(SpuEntity.class);
        if(StringUtil.isNotEmpty(spuDTO.getOrder())) example.setOrderByClause(spuDTO.getOrderByClause());

        //条件查询
        if(StringUtil.isNotEmpty(spuDTO.getTitle()))
            example.createCriteria().andLike("title","%"+spuDTO.getTitle()+"%");
        if(StringUtil.isNotEmpty(spuDTO.getSaleable()) && spuDTO.getSaleable() != 2)
            example.createCriteria().andEqualTo("saleable",spuDTO.getSaleable());

        List<SpuEntity> list = goodsMapper.selectByExample(example);

        List<SpuDTO> spuDTOList = list.stream().map(spuEntity -> {
            SpuDTO spuDTO1 = BaiDuBeanUtil.copyProperties(spuEntity, SpuDTO.class);

            //显示分类
            String categoryName = goodsMapper.getCategoryName(spuDTO1.getCid1(), spuDTO1.getCid2(), spuDTO1.getCid3());
            spuDTO1.setCategoryName(categoryName);
//            String categoryName = categoryMapper.selectByIdList(
//                    Arrays.asList(spuDTO1.getCid1(), spuDTO1.getCid2(), spuDTO1.getCid3()))
//                    .stream().map(category -> category.getName()).collect(Collectors.joining("/"));
//            spuDTO1.setCategoryName(categoryName);

            //显示品牌
            BrandDTO brandDTO = new BrandDTO();
            brandDTO.setId(spuEntity.getBrandId());
            Result<PageInfo<BrandEntity>> brandInfo = brandService.getBrandInfo(brandDTO);

            if (ObjectUtil.isNotNull(brandInfo)) {
                PageInfo<BrandEntity> data = brandInfo.getData();
                List<BrandEntity> list1 = data.getList();
                if (!list1.isEmpty() && list1.size() == 1) spuDTO1.setBrandName(list1.get(0).getName());
            }

            return spuDTO1;
        }).collect(Collectors.toList());

        PageInfo<SpuEntity> pageInfo = new PageInfo<>(list);

        return this.setResult(HTTPStatus.OK,pageInfo.getTotal() + "",spuDTOList);
    }

    @Transactional
    @Override
    public Result<JsonObject> goodsSave(SpuDTO spuDTO) {

        //新增spu
        SpuEntity spuEntity = BaiDuBeanUtil.copyProperties(spuDTO, SpuEntity.class);
        Date date = new Date();

        spuEntity.setValid(1);
        spuEntity.setSaleable(1);
        spuEntity.setCreateTime(date);
        spuEntity.setLastUpdateTime(date);
        goodsMapper.insertSelective(spuEntity);

        Integer spuId = spuEntity.getId();

        //新增spuDepail
        SpuDepailEntity spuDepailEntity = BaiDuBeanUtil.copyProperties(spuDTO.getSpuDetail(), SpuDepailEntity.class);
        spuDepailEntity.setSpuId(spuId);
        spuDepailMapper.insertSelective(spuDepailEntity);

        //新增sku和stock
        this.saveSkuAndStock(spuDTO.getSkus(),spuId,date);

        return this.setResultSuccess();
    }

    @Override
    public Result<SpuDepailEntity> getSpuDepailBySpu(Integer spuId) {

        SpuDepailEntity spuDepailEntity = spuDepailMapper.selectByPrimaryKey(spuId);

        return this.setResultSuccess(spuDepailEntity);
    }

    @Override
    public Result<List<SkuDTO>> getSkuBySpuId(Integer spuId) {

        List<SkuDTO> list = skuMapper.selectSkuAndStockBySpuId(spuId);

        return this.setResultSuccess(list);
    }

    @Transactional
    @Override
    public Result<JsonObject> goodsEdit(SpuDTO spuDTO) {

        Date date = new Date();

        //修改spu
        SpuEntity spuEntity = BaiDuBeanUtil.copyProperties(spuDTO, SpuEntity.class);
        spuEntity.setLastUpdateTime(date);
        goodsMapper.updateByPrimaryKeySelective(spuEntity);

        //修改detail
        spuDepailMapper.updateByPrimaryKeySelective(BaiDuBeanUtil.copyProperties(spuDTO.getSpuDetail(), SpuDepailEntity.class));

        //修改sku
        //先通过spuid删除sku
        //然后新增数据
        Example example = new Example(SkuEntity.class);
        example.createCriteria().andEqualTo("spuId",spuDTO.getId());
        List<Long> skuList = skuMapper.selectByExample(example).stream().map(sku -> sku.getId()).collect(Collectors.toList());
        skuMapper.deleteByIdList(skuList);

        //删除stock
        stockMapper.deleteByIdList(skuList);

        //新增新的sku和stock数据
        this.saveSkuAndStock(spuDTO.getSkus(),spuDTO.getId(),date);

        return setResultSuccess();
    }

    //数据重复新增sku和stock提出来
    private void saveSkuAndStock(List<SkuDTO> skus,Integer spuId,Date date){
        //新增sku
        skus.stream().forEach(skuDTO -> {
            SkuEntity skuEntity = BaiDuBeanUtil.copyProperties(skuDTO, SkuEntity.class);
            skuEntity.setSpuId(spuId);
            skuEntity.setCreateTime(date);
            skuEntity.setLastUpdateTime(date);
            skuMapper.insertSelective(skuEntity);

            //新增stock
            StockEntity stockEntity = new StockEntity();
            stockEntity.setSkuId(skuEntity.getId());
            stockEntity.setStock(skuDTO.getStock());
            stockMapper.insertSelective(stockEntity);
        });
    }
}
