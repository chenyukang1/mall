package com.cyk.mall.order.feign;

import com.cyk.mall.common.req.LockStockReq;
import com.cyk.mall.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

@Service
@FeignClient("stock-service")
public interface StockFeignService {

    @PostMapping("/stock/lockStock")
    R lockStock(@RequestBody LockStockReq lockStockReq);

    @RequestMapping("/stock/info/product/{productId}")
    R productInfo(@PathVariable("productId") Long productId);
}
