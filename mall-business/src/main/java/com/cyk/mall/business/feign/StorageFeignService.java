package com.cyk.mall.business.feign;

import com.cyk.mall.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * The class StorageFeignService.
 *
 * @author chenyukang
 * @email chen.yukang@qq.com
 * @date 2024/6/21
 **/
@FeignClient("storage-service")
@Service
public interface StorageFeignService {

    @GetMapping("/storage/storage/lockStock")
    R lockStock(@RequestParam("productId") long productId, @RequestParam("used") long used);
}
