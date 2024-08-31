package com.cyk.mall.stock.controller;

import com.cyk.mall.common.domain.req.LockStockReq;
import com.cyk.mall.common.utils.PageUtils;
import com.cyk.mall.common.utils.R;
import com.cyk.mall.stock.domain.po.StockEntity;
import com.cyk.mall.stock.service.StockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Map;

/**
 * 
 *
 * @author chenyk
 * @email chen.yukang@qq.com
 * @date 2024-06-21 16:29:27
 */
@RestController
@RequestMapping("/stock")
public class StockController {
    @Autowired
    private StockService stockService;

    @PostMapping("/lockStock")
    public R lockStock(LockStockReq lockStockReq) {
        boolean res = stockService.lockStock(lockStockReq);
        return res ? R.ok().put("res", true) : R.error("lock fail");
    }

    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = stockService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/product/{productId}")
    public R productInfo(@PathVariable("productId") Long productId){
        StockEntity storage = stockService.getByProductId(productId);

        return R.ok().put("storage", storage);
    }

    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
		StockEntity storage = stockService.getById(id);

        return R.ok().put("storage", storage);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody StockEntity storage){
		stockService.save(storage);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody StockEntity storage){
		stockService.updateById(storage);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids){
		stockService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
