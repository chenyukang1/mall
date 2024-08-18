package com.cyk.mall.order.controller;

import com.cyk.mall.common.utils.PageUtils;
import com.cyk.mall.common.utils.R;
import com.cyk.mall.order.domain.po.OrderEntity;
import com.cyk.mall.order.domain.req.SubmitOrderReq;
import com.cyk.mall.order.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Map;

/**
 * 
 *
 * @author chenyk
 * @email chen.yukang@qq.com
 * @date 2024-06-21 12:28:27
 */
@RestController
@RequestMapping("/order")
@Slf4j
public class OrderController {

    @Resource
    private OrderService orderService;

    @PostMapping("/submitOrder")
    public R submitOrder(@RequestBody SubmitOrderReq submitOrderReq) {
        log.info("提交订单 {}", submitOrderReq);
        orderService.submitOrder(submitOrderReq);
        return R.ok().put("res", "success");
    }

    @PostMapping("/submitOrderV2")
    public R submitOrderV2(@RequestBody SubmitOrderReq submitOrderReq) {
        log.info("提交订单 {}", submitOrderReq);
        orderService.submitOrderV2(submitOrderReq);
        return R.ok().put("res", "success");
    }

    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = orderService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
		OrderEntity order = orderService.getById(id);

        return R.ok().put("order", order);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody OrderEntity order){
		orderService.save(order);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody OrderEntity order){
		orderService.updateById(order);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids){
		orderService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
