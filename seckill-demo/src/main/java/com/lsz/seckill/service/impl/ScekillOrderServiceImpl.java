package com.lsz.seckill.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lsz.seckill.pojo.ScekillOrder;
import com.lsz.seckill.mapper.ScekillOrderMapper;
import com.lsz.seckill.pojo.User;
import com.lsz.seckill.service.IScekillOrderService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author lsz
 * @since 2021-08-20
 */
@Service
public class ScekillOrderServiceImpl extends ServiceImpl<ScekillOrderMapper, ScekillOrder> implements IScekillOrderService {


    @Autowired
    private ScekillOrderMapper scekillOrderMapper;
    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 获取秒杀结果
     * @param user
     * @param goodsId
     * @return orderId：成功；-1：秒杀失败；0：排队中。
     */
    @Override
    public Long getResult(User user, Long goodsId) {
        ScekillOrder scekillOrder = scekillOrderMapper.selectOne(new QueryWrapper<ScekillOrder>().eq(
                "user_id", user.getId()).eq(
                "goods_id", goodsId
        ));
        //订单不空，返回订单id
        if(null != scekillOrder){
            return scekillOrder.getOrderId();
        }else if (redisTemplate.hasKey("isStockEmpty:" + goodsId)){
            return -1L;
        }else {
            return 0L;
        }

    }
}
