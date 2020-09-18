package com.baidu;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;


/**
 * @ClassName RunSearcherServerApplication
 * @Description: TODO
 * @Author wanghaiyu
 * @Date 2020/9/16
 * @Version V1.0
 **/
//不加载自带的数据源配置
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
@EnableEurekaClient
@EnableFeignClients
public class RunSearcherServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(RunSearcherServerApplication.class);
    }

}
