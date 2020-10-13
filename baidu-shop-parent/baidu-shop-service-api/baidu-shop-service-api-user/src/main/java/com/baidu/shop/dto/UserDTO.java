package com.baidu.shop.dto;

import com.baidu.shop.validate.grop.BaiDuOperation;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Id;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * @ClassName UserDTO
 * @Description: TODO
 * @Author wanghaiyu
 * @Date 2020/10/13
 * @Version V1.0
 **/
@Data
@ApiModel(value = "用户DTO")
public class UserDTO {

    @ApiModelProperty(value = "用户主键",example = "1")
    @NotNull(message = "主键不能为空",groups = {BaiDuOperation.update.class})
    private Integer id;

    @ApiModelProperty(value = "账户")
    @NotNull(message = "账户不能为空",groups = {BaiDuOperation.add.class})
    private String username;

    @ApiModelProperty(value = "密码")
    @NotNull(message = "密码不能为空",groups = {BaiDuOperation.add.class})
    private String password;

    @ApiModelProperty(value = "手机号")
    @NotNull(message = "手机号不能为空",groups = {BaiDuOperation.add.class})
    private String phone;

    private Date created;

    private String salt;

}
