package com.cyk.mall.common.enums;

import lombok.Getter;

@Getter
public enum Exchanges {

    ORDER_EVENT_EXCHANGE("order-event-exchange"),
    STOCK_EVENT_EXCHANGE("stock-event-exchange");

    private final String name;

    Exchanges(String name) {
        this.name = name;
    }
}
