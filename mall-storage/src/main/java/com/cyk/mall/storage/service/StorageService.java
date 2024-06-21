package com.cyk.mall.storage.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cyk.mall.common.utils.PageUtils;
import com.cyk.mall.storage.entity.StorageEntity;

import java.util.Map;

/**
 * 
 *
 * @author chenyk
 * @email chen.yukang@qq.com
 * @date 2024-06-21 16:29:27
 */
public interface StorageService extends IService<StorageEntity> {

    PageUtils queryPage(Map<String, Object> params);

    boolean lockStock(long productId, long used);
}

