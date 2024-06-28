package com.cyk.mall.common.enums;

import lombok.Getter;

@Getter
public enum RoutingKey {
    ORDER_RELEASE_ORDER("order.release.order"),
    ORDER_CREATE_ORDER("order.create.order"),
    STOCK_RELEASE_STOCK("stock.release.stock"),
    STOCK_LOCK_STOCK("stock.lock.stock"),
    STOCK_RELEASE_ORDER("stock.release.order");

    private final String name;

    RoutingKey(String name) {
        this.name = name;
    }
}
