package com.tong.flyojbackendquestionservice;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@MapperScan("com.tong.flyojbackendquestionservice.mapper")
@EnableScheduling
@EnableAspectJAutoProxy(proxyTargetClass = true, exposeProxy = true)
@ComponentScan("com.tong")
@EnableDiscoveryClient
@EnableFeignClients(basePackages = {"com.tong.flyojbackendserviceclient.service"})
public class FlyojBackendQuestionServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(FlyojBackendQuestionServiceApplication.class, args);
    }

}
