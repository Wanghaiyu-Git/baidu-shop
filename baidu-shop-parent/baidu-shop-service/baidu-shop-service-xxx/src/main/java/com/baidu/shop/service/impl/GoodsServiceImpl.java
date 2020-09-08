package com.baidu.shop.service.impl;

import com.baidu.shop.base.BaseApiService;
import com.baidu.shop.base.Result;
import com.baidu.shop.dto.BrandDTO;
import com.baidu.shop.dto.SpuDTO;
import com.baidu.shop.entity.BrandEntity;
import com.baidu.shop.entity.CategoryEntity;
import com.baidu.shop.entity.SpuEntity;
import com.baidu.shop.mapper.CategoryMapper;
import com.baidu.shop.mapper.GoodsMapper;
import com.baidu.shop.service.BrandService;
import com.baidu.shop.service.GoodsService;
import com.baidu.shop.status.HTTPStatus;
import com.baidu.shop.utils.BaiDuBeanUtil;
import com.baidu.shop.utils.ObjectUtil;
import com.baidu.shop.utils.StringUtil;
import com.github.pagehelper.IPage;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.Arrays;
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
}
