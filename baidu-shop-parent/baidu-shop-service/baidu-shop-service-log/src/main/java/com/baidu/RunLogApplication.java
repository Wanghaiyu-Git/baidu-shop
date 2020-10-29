package com.baidu;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import tk.mybatis.spring.annotation.MapperScan;

/**
 * @ClassName RunLogApplication
 * @Description: TODO
 * @Author wanghaiyu
 * @Date 2020/10/28
 * @Version V1.0
 **/
@SpringBootApplication
@EnableEurekaClient
@MapperScan(value = "com.baidu.aop.mapper")
public class RunLogApplication {
    public static void main(String[] args) {
        SpringApplication.run(RunLogApplication.class);
    }
}
