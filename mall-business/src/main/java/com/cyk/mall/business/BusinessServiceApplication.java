package com.cyk.mall.business;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * The class OrderServiceApplication.
 *
 * @author chenyukang
 * @email chen.yukang@qq.com
 * @date 2024/6/21
 **/
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients("com.cyk.mall.business.feign")
public class BusinessServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(BusinessServiceApplication.class, args);
    }
}
