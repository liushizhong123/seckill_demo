package com.lsz.seckill.rabbitmq;


import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class MQsSender {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    /**
     * 发送秒杀消息
     * @param msg
     */
    public void sendSeckillMessage(String msg){
        log.info("发送消息：" + msg);
        //发送消息
        rabbitTemplate.convertAndSend("seckillExchange","seckill.message",msg);
    }
}
