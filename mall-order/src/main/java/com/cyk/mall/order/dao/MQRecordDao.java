package com.cyk.mall.order.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cyk.mall.order.domain.po.MQRecordEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * The class MQTaskDao.
 *
 * @author chenyukang
 * @email chen.yukang@qq.com
 * @date 2024/8/9
 */
@Mapper
public interface MQRecordDao extends BaseMapper<MQRecordEntity> {

}

