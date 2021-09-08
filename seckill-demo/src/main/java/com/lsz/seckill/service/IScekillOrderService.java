package com.lsz.seckill.service;

import com.lsz.seckill.pojo.ScekillOrder;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lsz.seckill.pojo.User;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author lsz
 * @since 2021-08-20
 */
public interface IScekillOrderService extends IService<ScekillOrder> {

    /**
     * 获取秒杀结果
     * @param user
     * @param goodsId
     * @return
     */
    Long getResult(User user, Long goodsId);
}
