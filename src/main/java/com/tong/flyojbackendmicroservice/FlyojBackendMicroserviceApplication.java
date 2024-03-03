package com.tong.flyojbackendmicroservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.tong.flyojbackenduserservice.mapper")
@EnableScheduling
@EnableAspectJAutoProxy(proxyTargetClass = true, exposeProxy = true)
@ComponentScan("com.tong")
public class FlyojBackendMicroserviceApplication {

    public static void main(String[] args) {
        SpringApplication.run(FlyojBackendMicroserviceApplication.class, args);
    }

}
