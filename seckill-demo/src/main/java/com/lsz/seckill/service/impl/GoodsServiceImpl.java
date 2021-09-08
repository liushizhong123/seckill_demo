package com.lsz.seckill.service.impl;

import com.lsz.seckill.pojo.Goods;
import com.lsz.seckill.mapper.GoodsMapper;
import com.lsz.seckill.service.IGoodsService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lsz.seckill.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author lsz
 * @since 2021-08-20
 */
@Service
public class GoodsServiceImpl extends ServiceImpl<GoodsMapper, Goods> implements IGoodsService {

    /**
     * 获取秒杀商品信息
     */
    @Autowired
    private GoodsMapper goodsMapper;

    @Override
    public List<GoodsVo> findGoodsVo() {
        return goodsMapper.findGoodsVo();
    }

    /**
     * 通过商品id获取商品详情
     * @param goodsId
     * @return
     */
    @Override
    public GoodsVo findGoodsVoByGoodsId(long goodsId) {
        return goodsMapper.findGoodsVoByGoodsId(goodsId);
    }
}
