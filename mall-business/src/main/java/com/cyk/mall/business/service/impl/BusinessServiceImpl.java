package com.cyk.mall.business.service.impl;

import com.cyk.mall.business.feign.OrderFeignService;
import com.cyk.mall.business.feign.StorageFeignService;
import com.cyk.mall.business.service.BusinessService;
import com.cyk.mall.common.utils.R;
import io.seata.core.context.RootContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class BusinessServiceImpl implements BusinessService {

    @Autowired
    private OrderFeignService orderFeignService;

    @Autowired
    private StorageFeignService storageFeignService;

    @Override
    public void submitOrder(long userId, long productId, long used) {
        log.info("submitOrder begin ... xid: {}", RootContext.getXID());
        R lockStock = storageFeignService.lockStock(productId, used);
        boolean lockRes = (boolean) lockStock.get("res");
        if (!lockRes) {
            throw new RuntimeException("lock stock fail");
        }
        R save = orderFeignService.save(userId, productId);
        boolean saveRes = (boolean) save.get("res");
        if (!saveRes) {
            throw new RuntimeException("save order fail");
        }
    }
}
