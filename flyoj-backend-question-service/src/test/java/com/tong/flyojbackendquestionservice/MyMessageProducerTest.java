package com.tong.flyojbackendquestionservice;

import com.tong.flyojbackendquestionservice.rabbitmq.MyMessageProducer;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
public class MyMessageProducerTest {

    @Resource
    private MyMessageProducer myMessageProducer;

    @Test
    void MessageTest(){
        myMessageProducer.sendMessage("code_exchange","myTest","hello");
    }
}
