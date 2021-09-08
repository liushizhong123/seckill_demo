package com.lsz.seckill.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lsz.seckill.pojo.Order;
import com.lsz.seckill.pojo.User;
import com.lsz.seckill.util.UserUtil;
import com.lsz.seckill.vo.GoodsVo;
import com.lsz.seckill.vo.OrderDetail;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author lsz
 * @since 2021-08-21
 */
public interface IOrderService extends IService<Order> {
    /**
     * 秒杀
     * @param user
     * @param goods
     * @return 订单表
     */
    Order seckill(User user, GoodsVo goods);

    /**
     * 订单详情
     * @param orderId
     * @return
     */
    OrderDetail detail(Long orderId);

    /**
     * 秒杀地址
     * @param user
     * @param goodsId
     * @return
     */

    String createPath(User user, Long goodsId);

    /**
     * 验证秒杀地址
     * @return
     */
    boolean checkPath(User user,Long goodsId,String path);

    /**
     * 验证验证码
     * @param user
     * @param goodsId
     * @param captcha
     * @return
     */
    boolean checkCaptcha(User user, Long goodsId, String captcha);
}
