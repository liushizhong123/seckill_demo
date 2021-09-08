package com.lsz.seckill.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lsz.seckill.pojo.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author lsz
 * @since 2021-08-19
 */

@Mapper
public interface UserMapper extends BaseMapper<User> {

}
