package com.lsz.seckill.service;

import com.lsz.seckill.pojo.Goods;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lsz.seckill.vo.GoodsVo;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author lsz
 * @since 2021-08-20
 */

public interface IGoodsService extends IService<Goods> {

    List<GoodsVo> findGoodsVo();

    GoodsVo findGoodsVoByGoodsId(long goodsId);
}
