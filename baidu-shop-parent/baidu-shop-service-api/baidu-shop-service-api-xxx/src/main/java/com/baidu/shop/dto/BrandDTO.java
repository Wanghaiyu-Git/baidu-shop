package com.baidu.shop.dto;

import com.baidu.shop.base.BaseDTO;
import com.baidu.shop.validate.grop.BaiDuOperation;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * @ClassName BrandDTO
 * @Description: TODO
 * @Author wanghaiyu
 * @Date 2020/8/31
 * @Version V1.0
 **/
@ApiModel(value = "品牌DTO")
@Data
public class BrandDTO extends BaseDTO {

    @ApiModelProperty(value = "品牌主键",example = "1")
    @NotNull(message = "主键不能为空", groups = {BaiDuOperation.update.class})
    private Integer id;

    @ApiModelProperty(value = "品牌名称")
    @NotEmpty(message = "名牌名称不能为空", groups = {BaiDuOperation.add.class,BaiDuOperation.update.class})
    private String name;

    @ApiModelProperty(value = "品牌图片")
    private String image;

    @ApiModelProperty(value = "品牌首字母")
    private Character letter;

    @ApiModelProperty(value = "品牌分类信息")
    @NotEmpty(message = "品牌分类信息不能为空",groups = {BaiDuOperation.add.class})
    private String category;

}
