package com.baidu;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import tk.mybatis.spring.annotation.MapperScan;

/**
 * @ClassName RunCarServerApplication
 * @Description: TODO
 * @Author wanghaiyu
 * @Date 2020/10/19
 * @Version V1.0
 **/
@SpringBootApplication
@EnableEurekaClient
@EnableFeignClients
@MapperScan(value = "com.baidu.aop.mapper")
public class RunCarServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(RunCarServerApplication.class);
    }
}
