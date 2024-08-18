package com.cyk.mall.order.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cyk.mall.order.dao.MQTaskDao;
import com.cyk.mall.order.domain.po.MQTaskEntity;
import com.cyk.mall.order.service.TaskService;
import org.springframework.stereotype.Service;

/**
 * The class TaskServiceImpl.
 *
 * @author chenyukang
 * @email chen.yukang@qq.com
 * @date 2024/8/9
 */
@Service
public class TaskServiceImpl extends ServiceImpl<MQTaskDao, MQTaskEntity> implements TaskService {
}
