package com.cyk.mall.stock.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cyk.mall.common.enums.Exchanges;
import com.cyk.mall.common.enums.RoutingKey;
import com.cyk.mall.common.to.OrderTo;
import com.cyk.mall.common.to.StockTo;
import com.cyk.mall.common.utils.PageUtils;
import com.cyk.mall.common.utils.Query;
import com.cyk.mall.common.utils.R;
import com.cyk.mall.stock.dao.StockDao;
import com.cyk.mall.stock.entity.StockEntity;
import com.cyk.mall.stock.feign.OrderFeignService;
import com.cyk.mall.stock.service.StockService;
import com.cyk.mall.stock.to.StockLockTo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;


@Service("storageService")
@Slf4j
public class StockServiceImpl extends ServiceImpl<StockDao, StockEntity> implements StockService {

    @Autowired
    private StockDao stockDao;

    @Autowired
    private OrderFeignService orderFeignService;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<StockEntity> page = this.page(
                new Query<StockEntity>().getPage(params),
                new QueryWrapper<StockEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public boolean lockStock(long productId, long used, long orderSn) {
        if (stockDao.lockStock(productId, used) > 0) {
            Integer version = getOne(new QueryWrapper<StockEntity>().eq("product_id", productId)).getVersion();
            StockLockTo stockLockTo = new StockLockTo();
            stockLockTo.setProductId(productId);
            stockLockTo.setUsed(used);
            stockLockTo.setOrderSn(orderSn);
            stockLockTo.setVersion(version);

            // 发送消息到mq延迟队列，判断是否需要回滚库存
            // - 订单不存在，回滚
            // - 订单已取消，回滚
            rabbitTemplate.convertAndSend(Exchanges.STOCK_EVENT_EXCHANGE.getName(),
                    RoutingKey.STOCK_LOCK_STOCK.getName(),
                    stockLockTo);
            return true;
        }
        return false;
    }

    @Override
    public void unlockStock(StockLockTo stockLockTo) {
        R res = orderFeignService.info(stockLockTo.getOrderSn());
        if (res.getCode() == 0) {
            OrderTo orderTo = res.getData("order", new TypeReference<OrderTo>(){});
            // 1、订单不存在或订单已取消，回滚库存
            // 2、数据库乐观锁处理幂等性问题
            if (orderTo == null || orderTo.getStatus() == 3) {
                int count = stockDao.unlockStock(stockLockTo.getProductId(), stockLockTo.getUsed(), stockLockTo.getVersion());
                if (count > 0) {
                    log.info("订单关闭，库存回滚成功 productId: {}, version: {}", stockLockTo.getProductId(), stockLockTo.getVersion());
                } else {
                    log.info("订单关闭，库存回滚失败 productId: {}, version: {}", stockLockTo.getProductId(), stockLockTo.getVersion());
                }
            }
        }
    }

    @Override
    public void unlockStock(StockTo stockTo) {
        // 1、这里是关单后发的消息，直接去回滚库存
        // 2、数据库乐观锁处理幂等性问题
        int count = stockDao.unlockStock(stockTo.getProductId(), stockTo.getRollback(), stockTo.getVersion());
        if (count > 0) {
            log.info("订单关闭，库存回滚成功 productId: {}, version: {}", stockTo.getProductId(), stockTo.getVersion());
        } else {
            log.info("订单关闭，库存回滚失败 productId: {}, version: {}", stockTo.getProductId(), stockTo.getVersion());
        }
    }

    @Override
    public StockEntity getByProductId(Long productId) {
        return getOne(new QueryWrapper<StockEntity>().eq("product_id", productId));
    }



}