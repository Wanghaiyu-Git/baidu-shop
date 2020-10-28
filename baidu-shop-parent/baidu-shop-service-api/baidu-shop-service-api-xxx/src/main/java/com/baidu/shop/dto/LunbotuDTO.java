package com.baidu.shop.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Id;

/**
 * @ClassName LunbotuDTO
 * @Description: TODO
 * @Author wanghaiyu
 * @Date 2020/10/27
 * @Version V1.0
 **/
@ApiModel(value = "轮播图数据传输类")
@Data
public class LunbotuDTO {

    @ApiModelProperty(value = "主键",example = "1")
    private Integer id;

    @ApiModelProperty(value = "图片地址")
    private String image;

    @ApiModelProperty(value = "跳转地址")
    private String tiaozhuan;
}
