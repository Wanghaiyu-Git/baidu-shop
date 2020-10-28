package com.baidu.shop.mapper;

import com.baidu.shop.entity.LunboEntity;
import tk.mybatis.mapper.additional.idlist.SelectByIdListMapper;
import tk.mybatis.mapper.common.Mapper;

/**
 * @ClassName LunboMapper
 * @Description: TODO
 * @Author wanghaiyu
 * @Date 2020/10/27
 * @Version V1.0
 **/
public interface LunboMapper extends Mapper<LunboEntity> , SelectByIdListMapper<LunboEntity,Integer> {
}
