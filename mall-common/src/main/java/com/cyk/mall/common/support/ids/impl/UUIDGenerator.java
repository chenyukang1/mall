package com.cyk.mall.common.support.ids.impl;

import com.cyk.mall.common.domain.constant.Constants;
import com.cyk.mall.common.support.ids.IIdGenerator;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * The class UUIDGenerator.
 *
 * @author chenyukang
 * @email chen.yukang@qq.com
 * @date 2024/8/19
 */
@Component(Constants.IdGeneratorConstants.UUID_GENERATOR)
public class UUIDGenerator implements IIdGenerator {

    @Override
    public String nextId() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}
