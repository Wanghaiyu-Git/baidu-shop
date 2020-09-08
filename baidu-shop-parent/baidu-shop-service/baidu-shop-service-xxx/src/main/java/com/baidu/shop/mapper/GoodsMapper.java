package com.baidu.shop.mapper;

import com.baidu.shop.entity.CategoryEntity;
import com.baidu.shop.entity.SpuEntity;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * @ClassName GoodsMapper
 * @Description: TODO
 * @Author wanghaiyu
 * @Date 2020/9/7
 * @Version V1.0
 **/
public interface GoodsMapper extends Mapper<SpuEntity> {

//    @Select(value = "select s.*,b.`name` as brandName from tb_spu s, tb_brand b where s.brand_id = b.id")
//    List<SpuEntity> getBrandName(Integer brandId);

    @Select(value = "select group_concat(name separator '/') as categoryName from tb_category where id in (#{cid1},#{cid2},#{cid3})")
    String getCategoryName(Integer cid1,Integer cid2,Integer cid3);
}
