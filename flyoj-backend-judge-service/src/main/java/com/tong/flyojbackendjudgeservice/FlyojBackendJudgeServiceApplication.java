package com.tong.flyojbackendjudgeservice;

import com.tong.flyojbackendjudgeservice.rabbitmq.MqInitMain;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableAspectJAutoProxy(proxyTargetClass = true, exposeProxy = true)
@ComponentScan("com.tong")
@EnableDiscoveryClient
@EnableFeignClients(basePackages = {"com.tong.flyojbackendserviceclient.service"})
public class FlyojBackendJudgeServiceApplication {

    public static void main(String[] args) {
        MqInitMain.doInit();
        SpringApplication.run(FlyojBackendJudgeServiceApplication.class, args);
    }

}
