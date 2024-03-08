package com.tong.flyojbackendjudgeservice.rabbitmq;


import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 用于创建测试程序用到的交换机和队列（只用在程序启动前执行一次）
 */
@Component
@Slf4j
public class MqInitMain {

    /**
     * 执行创建rabbitmq交换机和消息队列的方法
     * 会在application调用，自动执行
     */
    public static void doInit(){
        try {
            // 创建连接
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost("43.139.98.22");
            factory.setUsername("rabbitmq");
            factory.setPassword("rabbitmq");
            Connection connection = factory.newConnection();
            Channel channel = connection.createChannel();
            // 创建交换机
            String EXCHANGE_NAME = "code_exchange";
            channel.exchangeDeclare(EXCHANGE_NAME, "direct");

            // 创建队列，随机分配一个队列名称
            String queueName = "code_queue";
            channel.queueDeclare(queueName, true, false, false, null);
            channel.queueBind(queueName, EXCHANGE_NAME, "my_routingKey");
            log.info("消息队列启动成功");
        } catch (Exception e) {
            log.error("消息队列启动失败:"+ e);
        }
    }

    public static void main(String[] args) {
        doInit();
    }
}