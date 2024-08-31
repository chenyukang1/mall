package com.cyk.mall.common.support.ids.factory;

import com.cyk.mall.common.domain.constant.Constants;
import com.cyk.mall.common.support.ids.IIdGenerator;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * The class IdGeneratorFactory.
 *
 * @author chenyukang
 * @email chen.yukang@qq.com
 * @date 2024/8/19
 */
@Component
public class IdGeneratorFactory {

    private final Map<String, IIdGenerator> idGeneratorMap;

    public IdGeneratorFactory(Map<String, IIdGenerator> idGeneratorMap) {
        this.idGeneratorMap = idGeneratorMap;
    }

    public IIdGenerator get(Constants.IdGeneratorType idGeneratorType) {
        return idGeneratorMap.get(idGeneratorType.getType());
    }
}
