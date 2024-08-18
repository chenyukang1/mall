package com.cyk.mall.stock.domain.to;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StockLockTo {

    private long productId;
    private long used;
    private long orderSn;
    private int version;
}
