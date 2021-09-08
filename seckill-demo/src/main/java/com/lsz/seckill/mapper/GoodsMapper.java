package com.lsz.seckill.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lsz.seckill.pojo.Goods;
import com.lsz.seckill.vo.GoodsVo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author lsz
 * @since 2021-08-20
 */
@Mapper
public interface GoodsMapper extends BaseMapper<Goods> {

    List<GoodsVo> findGoodsVo();

    GoodsVo findGoodsVoByGoodsId(long goodsId);
}
