package com.baidu.shop.dto;

import com.baidu.shop.validate.grop.BaiDuOperation;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * @ClassName SpecParamDTO
 * @Description: TODO
 * @Author wanghaiyu
 * @Date 2020/9/3
 * @Version V1.0
 **/
@ApiModel(value = "规格参数数据传输DTO")
@Data
public class SpecParamDTO {

    @ApiModelProperty(value = "主键",example = "1")
    @NotNull(message = "主键不能为空",groups = {BaiDuOperation.update.class})
    private Integer id;

    @ApiModelProperty(value = "商品分类id",example = "1")
    private Integer cid;

    @ApiModelProperty(value = "规格组id")
    private Integer groupId;

    @ApiModelProperty(value = "参数名")
    private String name;

    @ApiModelProperty(value = "是否是数字类型参数,1->true或0->false",example = "0")
    @NotNull(message = "是否是数字类型参数不能为空",groups = {BaiDuOperation.add.class,BaiDuOperation.update.class})
    private Boolean numeric;

    @ApiModelProperty(value = "数字类型参数的单位")
    private String unit;

    @ApiModelProperty(value = "是否是sku通用属性,1->true或0->false",example = "0")
    @NotNull(message = "是否是sku通用属性不能为空",groups = {BaiDuOperation.add.class,BaiDuOperation.update.class})
    private Boolean generic;

    @ApiModelProperty(value = "是否用于搜索过滤,1->true或0->false",example = "0")
    @NotNull(message = "是否用于搜索过滤不能为空",groups = {BaiDuOperation.add.class,BaiDuOperation.update.class})
    private Boolean searching;

    @ApiModelProperty(value = "数值类型参数")
    private String segments;
}
