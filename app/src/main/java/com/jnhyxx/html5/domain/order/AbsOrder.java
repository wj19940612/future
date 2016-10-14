package com.jnhyxx.html5.domain.order;

public class AbsOrder {

    public static final int ORDER_STATUS_UNPAID = 0; // 未支付,待支付,理论上不会存在
    public static final int ORDER_STATUS_PAID_UNHOLDING = 1; // 已支付,待持仓
    public static final int ORDER_STATUS_HOLDING = 2; // 持仓中
    public static final int ORDER_STATUS_CLOSING = 3; // 平仓处理中
    public static final int ORDER_STATUS_SETTLED = 4; // 已结算

    public static final int DIRECTION_LONG = 1;
    public static final int DIRECTION_SHORT = 0;

    public static final int SELL_OUT_MAKERT_PRICE = 0;
    public static final int SELL_OUT_STOP_PROFIT = 1;
    public static final int SELL_OUT_STOP_LOSS = 2;
    public static final int SELL_OUT_SYSTEM_CLEAR = 3;
}
