package com.cyk.mall.storage.controller;

import com.cyk.mall.common.utils.PageUtils;
import com.cyk.mall.common.utils.R;
import com.cyk.mall.storage.entity.StorageEntity;
import com.cyk.mall.storage.service.StorageService;
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
@RequestMapping("storage/storage")
public class StorageController {
    @Autowired
    private StorageService storageService;

    @GetMapping("/lockStock")
    public R lockStock(long productId, long used) {
        boolean res = storageService.lockStock(productId, used);
        return R.ok().put("res", res);
    }

    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = storageService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
		StorageEntity storage = storageService.getById(id);

        return R.ok().put("storage", storage);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody StorageEntity storage){
		storageService.save(storage);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody StorageEntity storage){
		storageService.updateById(storage);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids){
		storageService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
