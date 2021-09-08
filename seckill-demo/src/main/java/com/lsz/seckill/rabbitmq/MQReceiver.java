package com.lsz.seckill.rabbitmq;

import com.alibaba.fastjson.JSON;
import com.lsz.seckill.pojo.Order;
import com.lsz.seckill.pojo.SeckillGoods;
import com.lsz.seckill.pojo.SeckillMessage;
import com.lsz.seckill.pojo.User;
import com.lsz.seckill.service.IGoodsService;
import com.lsz.seckill.service.IOrderService;
import com.lsz.seckill.vo.GoodsVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.ReactiveStreamCommands;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class MQReceiver {

    @Autowired
    private IGoodsService goodsService;
    @Autowired
    private IOrderService orderService;
    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 接受消息
     * @param msg
     */
//    @RabbitListener(queues = "queue")
//    public void receive(Object msg){
//        log.info("接收消息：" + msg);
//    }
//
//    @RabbitListener(queues = "queue_fanout01")
//    public void receive01(Object msg){
//        log.info("QUEUE01接受消息：" + msg);
//    }
//
//    @RabbitListener(queues = "queue_fanout02")
//    public void receive02(Object msg){
//        log.info("QUEUE02接受消息：" + msg);
//    }

    @RabbitListener(queues = "seckillQueue")
    public void receive(String msg){
        log.info("接收到的消息：" + msg);
        SeckillMessage seckillMessage = JSON.parseObject(msg,SeckillMessage.class);
        Long goodsId = seckillMessage.getGoodsId();
        User user = seckillMessage.getUser();
        GoodsVo goodsVo = goodsService.findGoodsVoByGoodsId(goodsId);
        //判断库存
        if(goodsVo.getStockCount() < 1){
            return;
        }
        //判断重复抢购
        SeckillGoods seckillGoods =
                (SeckillGoods) redisTemplate.opsForValue().get("order:" + user.getId() + ":" + goodsId);
        if(seckillGoods != null){
            return;
        }
        //下单
        orderService.seckill(user,goodsVo);
    }
}
