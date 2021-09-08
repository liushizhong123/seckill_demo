package com.lsz.seckill.pojo;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * <p>
 * 
 * </p>
 *
 * @author lsz
 * @since 2021-08-20
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("t_scekill_order")
public class ScekillOrder implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private Long userId;

    private Long orderId;

    private Long goodsId;


}
