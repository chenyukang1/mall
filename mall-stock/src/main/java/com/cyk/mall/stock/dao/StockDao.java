package com.cyk.mall.stock.dao;

import com.cyk.mall.stock.entity.StockEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 
 * 
 * @author chenyk
 * @email chen.yukang@qq.com
 * @date 2024-06-21 16:29:27
 */
@Mapper
public interface StockDao extends BaseMapper<StockEntity> {

    int lockStock(@Param("productId") long productId, @Param("num") long used);

    int unlockStock(@Param("productId") long productId, @Param("num") long used, @Param("version") int version);
}
