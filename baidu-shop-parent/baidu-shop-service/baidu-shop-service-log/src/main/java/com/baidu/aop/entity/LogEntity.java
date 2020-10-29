package com.baidu.aop.entity;

import lombok.Data;

import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * @ClassName entity
 * @Description: TODO
 * @Author wanghaiyu
 * @Date 2020/10/28
 * @Version V1.0
 **/
@Table(name = "tb_log")
@Data
public class LogEntity {

    @Id
    private Long id;

    private String username;

    private String ip;

    private String type;

    private String description;

    private String model;

    private Date operationTime;

    private String url;

}
