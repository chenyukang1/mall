package com.cyk.mall.order.feign;

import com.cyk.mall.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Service
@FeignClient("storage-service")
public interface StorageFeignService {

    @GetMapping("/stock/lockStock")
    R lockStock(@RequestParam("productId") long productId, @RequestParam("used") long used,
                @RequestParam("orderSn") long orderSn);

    @RequestMapping("/stock/info/product/{productId}")
    R productInfo(@PathVariable("productId") Long productId);
}
