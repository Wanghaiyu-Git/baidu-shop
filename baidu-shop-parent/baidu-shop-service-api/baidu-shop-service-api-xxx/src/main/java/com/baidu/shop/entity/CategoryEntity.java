package com.baidu.shop.entity;

import com.baidu.shop.validate.grop.BaiDuOperation;
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
    @NotNull(message = "ID不能为空", groups = {BaiDuOperation.update.class})
    private Integer id;

    @ApiModelProperty(value = "类目名称")
    @NotEmpty(message = "类目名称不能为空", groups = {BaiDuOperation.add.class, BaiDuOperation.update.class})
    private String name;

    @ApiModelProperty(value = "父级分类",example = "1")
    @NotNull(message = "父级分类不能为空", groups = {BaiDuOperation.add.class})
    private Integer parentId;

    @ApiModelProperty(value = "是否为父节点,0为否,1为是",example = "0")
    @NotNull(message = "是否为父节点不能为空", groups = {BaiDuOperation.add.class})
    private Integer isParent;

    @ApiModelProperty(value = "排序指数,越小越靠前",example = "1")
    @NotNull(message = "排序指数不能为空", groups = {BaiDuOperation.add.class})
    private Integer sort;

}
