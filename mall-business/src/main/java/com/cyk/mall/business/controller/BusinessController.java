package com.cyk.mall.business.controller;

import com.cyk.mall.business.service.BusinessService;
import com.cyk.mall.common.utils.R;
import io.seata.spring.annotation.GlobalTransactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * The class BusinessController.
 *
 * @author chenyukang
 * @email chen.yukang@qq.com
 * @date 2024/6/21
 **/
@Controller
@RequestMapping("/business")
public class BusinessController {

    @Autowired
    BusinessService businessService;

    @GlobalTransactional(timeoutMills = 300000)
    @ResponseBody
    @GetMapping("/submitOrder")
    public R submitOrder(long userId, long productId, long used) {
        businessService.submitOrder(userId, productId, used);
//        R lockStock = storageFeignService.lockStock(productId, used);
//        boolean lockRes = (boolean) lockStock.get("res");
//        if (!lockRes) {
//            throw new RuntimeException("lock stock fail");
//        }
//
//        R save = orderFeignService.save(userId, productId);
//        boolean saveRes = (boolean) save.get("res");
//        if (!saveRes) {
//            throw new RuntimeException("save order fail");
//        }

        return R.ok().put("res", "success");
    }
}
