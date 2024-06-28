package com.cyk.mall.common.enums;

import lombok.Getter;

@Getter
public enum Queues {

    ORDER_DELAY_QUEUE("order.delay.queue"),
    ORDER_RELEASE_QUEUE("order.release.queue"),
    STOCK_DELAY_QUEUE("stock.delay.queue"),
    STOCK_RELEASE_QUEUE("stock.release.queue");

    private final String name;

    Queues(String name) {
        this.name = name;
    }
}
