package com.baidu.shop.entity;

import lombok.Data;

import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * @ClassName DailyRecommendationEntity
 * @Description: TODO
 * @Author wanghaiyu
 * @Date 2020/10/27
 * @Version V1.0
 **/
@Table(name = "tb_daily_recommendation")
@Data
public class DailyRecommendationEntity {

    @Id
    private Integer id;

    private String categoryName;

    private String brandName;

    private String title;

    private Long skuId;

    private Date createTime;
}
