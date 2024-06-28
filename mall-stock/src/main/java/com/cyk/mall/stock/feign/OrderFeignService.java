package com.cyk.mall.stock.feign;

import com.cyk.mall.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Service
@FeignClient("order-service")
public interface OrderFeignService {

    @GetMapping("/order/info/{orderSn}")
    R info(@PathVariable("orderSn") long orderSn);
}
