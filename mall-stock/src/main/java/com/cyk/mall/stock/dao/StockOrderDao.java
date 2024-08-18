package com.cyk.mall.stock.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cyk.mall.stock.domain.po.StockOrderEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * The interface StockOrderDao.
 *
 * @author chenyukang
 * @email chen.yukang@qq.com
 * @date 2024/8/10
 */
@Mapper
public interface StockOrderDao extends BaseMapper<StockOrderEntity> {

}
