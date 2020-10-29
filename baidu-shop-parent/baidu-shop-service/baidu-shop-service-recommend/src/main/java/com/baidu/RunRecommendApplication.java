package com.baidu;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import tk.mybatis.spring.annotation.MapperScan;

/**
 * @ClassName RunRecommendApplication
 * @Description: TODO
 * @Author wanghaiyu
 * @Date 2020/10/29
 * @Version V1.0
 **/
@SpringBootApplication
@EnableEurekaClient
@MapperScan(value = "com.baidu")
public class RunRecommendApplication {
    public static void main(String[] args) {
        SpringApplication.run(RunRecommendApplication.class);
    }
}
