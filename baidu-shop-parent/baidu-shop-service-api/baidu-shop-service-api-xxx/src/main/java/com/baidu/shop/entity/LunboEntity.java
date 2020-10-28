package com.baidu.shop.entity;

import lombok.Data;

import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @ClassName LunboEntity
 * @Description: TODO
 * @Author wanghaiyu
 * @Date 2020/10/27
 * @Version V1.0
 **/
@Table(name = "tb_lunbo")
@Data
public class LunboEntity {

    @Id
    private Integer id;

    private String image;

    private String tiaozhuan;
}
