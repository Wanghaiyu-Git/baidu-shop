package com.baidu.shop.entity;

import lombok.Data;

import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @ClassName SpuDepailEntity
 * @Description: TODO
 * @Author wanghaiyu
 * @Date 2020/9/8
 * @Version V1.0
 **/
@Table(name = "tb_spu_detail")
@Data
public class SpuDepailEntity {

    @Id
    private Integer spuId;

    private String description;

    private String genericSpec;

    private String specialSpec;

    private String packingList;

    private String afterService;
}
