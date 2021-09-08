package com.lsz.seckill.vo;

import com.lsz.seckill.pojo.Order;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderDetail {

    private Order order;

    private GoodsVo goodsVo;
}
