package com.cyk.mall.stock.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cyk.mall.common.to.StockTo;
import com.cyk.mall.common.utils.PageUtils;
import com.cyk.mall.stock.entity.StockEntity;
import com.cyk.mall.stock.to.StockLockTo;

import java.util.Map;

/**
 * 
 *
 * @author chenyk
 * @email chen.yukang@qq.com
 * @date 2024-06-21 16:29:27
 */
public interface StockService extends IService<StockEntity> {

    PageUtils queryPage(Map<String, Object> params);

    boolean lockStock(long productId, long used, long orderSn);

    void unlockStock(StockLockTo stockLockTo);

    void unlockStock(StockTo stockTo);

    StockEntity getByProductId(Long productId);

}

