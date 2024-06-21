package com.cyk.mall.business.feign;

import com.cyk.mall.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * The interface OrderFeignService.
 *
 * @author chenyukang
 * @email chen.yukang@qq.com
 * @date 2024/6/21
 **/
@FeignClient("order-service")
@Service
public interface OrderFeignService {

    @GetMapping("/order/order/save")
    R save(@RequestParam("userId") long userId, @RequestParam("productId") long productId);
}
