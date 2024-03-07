package com.tong.flyojbackenduserservice;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 主类（项目启动入口）
 *
 * @author tong
 *
 */

@SpringBootApplication
@MapperScan("com.tong.flyojbackenduserservice.mapper")
@EnableScheduling
@EnableAspectJAutoProxy(proxyTargetClass = true, exposeProxy = true)
@ComponentScan("com.tong")
@EnableDiscoveryClient
@EnableFeignClients(basePackages = {"com.tong.flyojbackendserviceclient.service"})
public class FlyojBackendUserServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(FlyojBackendUserServiceApplication.class, args);
    }

}
