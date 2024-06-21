package com.cyk.mall.storage.dao;

import com.cyk.mall.storage.entity.StorageEntity;
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
public interface StorageDao extends BaseMapper<StorageEntity> {

    int lockStock(@Param("productId") long productId, @Param("num") long used);
}
