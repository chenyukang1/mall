package com.cyk.mall.order.holder;

import lombok.Getter;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * The class ApplicationContextHolder.
 *
 * @author chenyukang
 * @email chen.yukang@qq.com
 * @date 2024/8/10
 */
@Getter
@Component
public class ApplicationContextHolder implements ApplicationContextAware {

    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

}
