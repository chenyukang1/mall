package com.cyk.mall.order;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * The class OrderServiceApplication.
 *
 * @author chenyukang
 * @email chen.yukang@qq.com
 * @date 2024/6/21
 **/
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients("com.cyk.mall.order.feign")
@EnableScheduling
public class OrderServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrderServiceApplication.class, args);
    }
}
