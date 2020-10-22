package com.baidu.shop.mapper;

import com.baidu.shop.dto.SkuDTO;
import com.baidu.shop.entity.OrderEntity;
import org.apache.ibatis.annotations.Update;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * @ClassName OrderMapper
 * @Description: TODO
 * @Author wanghaiyu
 * @Date 2020/10/21
 * @Version V1.0
 **/
public interface OrderMapper extends Mapper<OrderEntity> {

    @Update(value = "update tb_stock t set t.stock = (\n" +
            "\t ( select * from ( select stock from tb_stock where sku_id = #{skuId} ) a ) - #{num} \n" +
            ") \n" +
            "where t.sku_id =  #{skuId}")
    void updateStockBySpuId(Long skuId,Integer num);
}
