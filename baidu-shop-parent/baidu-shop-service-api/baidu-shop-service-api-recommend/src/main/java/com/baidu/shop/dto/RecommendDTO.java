package com.baidu.shop.dto;

import io.swagger.annotations.Api;
import lombok.Data;

/**
 * @ClassName RecommendDTO
 * @Description: TODO
 * @Author wanghaiyu
 * @Date 2020/10/29
 * @Version V1.0
 **/
@Data
public class RecommendDTO {

    private Integer spuId;

    private String images;

    private String title;

    private Long price;
}
