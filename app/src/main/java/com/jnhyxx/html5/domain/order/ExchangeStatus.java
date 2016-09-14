package com.jnhyxx.html5.domain.order;

import java.io.Serializable;

public class ExchangeStatus implements Serializable {

    public static final String EX_EXCHANGE_STATUS = "exchangeStatus";

    /**
     * exchangeId: 9
     * nextTime : 16:30:02
     * isCycle : false
     * status : true
     */

    private int exchangeId;
    private String nextTime;
    private boolean isCycle;
    private boolean status;

    public int getExchangeId() {
        return exchangeId;
    }

    public void setExchangeId(int exchangeId) {
        this.exchangeId = exchangeId;
    }

    public String getNextTime() {
        return nextTime;
    }

    public void setNextTime(String nextTime) {
        this.nextTime = nextTime;
    }

    public boolean isIsCycle() {
        return isCycle;
    }

    public void setIsCycle(boolean isCycle) {
        this.isCycle = isCycle;
    }

    public boolean isTradeable() {
        return status;
    }

    public void setTradeable(boolean tradeable) {
        this.status = tradeable;
    }
}
