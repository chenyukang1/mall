package com.cyk.mall.storage.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cyk.mall.common.utils.PageUtils;
import com.cyk.mall.common.utils.Query;
import com.cyk.mall.storage.dao.StorageDao;
import com.cyk.mall.storage.entity.StorageEntity;
import com.cyk.mall.storage.service.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;


@Service("storageService")
public class StorageServiceImpl extends ServiceImpl<StorageDao, StorageEntity> implements StorageService {

    @Autowired
    private StorageDao storageDao;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<StorageEntity> page = this.page(
                new Query<StorageEntity>().getPage(params),
                new QueryWrapper<StorageEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public boolean lockStock(long productId, long used) {
        return storageDao.lockStock(productId, used) > 0;
    }

}