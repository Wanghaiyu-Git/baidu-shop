package com.baidu;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import tk.mybatis.spring.annotation.MapperScan;


/**
 * @ClassName RunSearcherServerApplication
 * @Description: TODO
 * @Author wanghaiyu
 * @Date 2020/9/16
 * @Version V1.0
 **/
//不加载自带的数据源配置
@SpringBootApplication
@EnableEurekaClient
@EnableFeignClients
@MapperScan(value = "com.baidu")
public class RunSearcherServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(RunSearcherServerApplication.class);
    }

}
