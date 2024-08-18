package com.cyk.mall.stock.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cyk.mall.common.req.LockStockReq;
import com.cyk.mall.common.to.OrderTo;
import com.cyk.mall.common.to.StockTo;
import com.cyk.mall.common.utils.PageUtils;
import com.cyk.mall.common.utils.Query;
import com.cyk.mall.common.utils.R;
import com.cyk.mall.stock.dao.StockDao;
import com.cyk.mall.stock.dao.StockOrderDao;
import com.cyk.mall.stock.domain.po.StockEntity;
import com.cyk.mall.stock.domain.po.StockOrderEntity;
import com.cyk.mall.stock.domain.to.StockLockTo;
import com.cyk.mall.stock.feign.OrderFeignService;
import com.cyk.mall.stock.service.StockService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Map;


@Service("storageService")
@Slf4j
public class StockServiceImpl extends ServiceImpl<StockDao, StockEntity> implements StockService {

    @Resource
    private StockDao stockDao;

    @Resource
    private StockOrderDao stockOrderDao;

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
    @Transactional
    public boolean lockStock(LockStockReq lockStockReq) {
        if (stockDao.lockStock(lockStockReq.getSku(), lockStockReq.getLockCount()) > 0) {
            StockOrderEntity stockOrderEntity = new StockOrderEntity();
            stockOrderEntity.setSku(lockStockReq.getSku());
            stockOrderEntity.setOrderSn(lockStockReq.getOrderSn());
            stockOrderEntity.setStockSubtractionCount(lockStockReq.getLockCount());
            stockOrderDao.insert(stockOrderEntity);
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