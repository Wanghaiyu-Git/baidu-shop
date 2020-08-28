package com.baidu.shop.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * @ClassName CategoryEntity
 * @Description: TODO
 * @Author wanghaiyu
 * @Date 2020/8/27
 * @Version V1.0
 **/
@ApiModel(value = "分类实体类")
@Data
@Table(name = "tb_category")
public class CategoryEntity {

    @Id
    @ApiModelProperty(value = "主键" ,example = "1")
    @NotNull
    private Integer id;

    @ApiModelProperty(value = "类目名称")
    @NotEmpty
    private String name;

    @ApiModelProperty(value = "父类ID",example = "1")
    @NotNull
    private Integer parentId;

    @ApiModelProperty(value = "是否为父节点,0为否,1为是",example = "0")
    @NotNull
    private Integer isParent;

    @ApiModelProperty(value = "排序指数,越小越靠前",example = "1")
    @NotNull
    private Integer sort;

}
