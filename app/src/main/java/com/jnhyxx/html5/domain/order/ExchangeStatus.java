package com.jnhyxx.html5.domain.order;

import com.jnhyxx.html5.domain.local.SysTime;
import com.jnhyxx.html5.utils.StrFormatter;
import com.johnz.kutils.DateUtil;

public class ExchangeStatus {

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
    private long exchangeNextTime;
    private long inventoryNextTime;

    public int getExchangeId() {
        return exchangeId;
    }

    public void setExchangeId(int exchangeId) {
        this.exchangeId = exchangeId;
    }

    public String getNextTime() {
        return createNextTime();
    }

    private String createNextTime() {
        long now = SysTime.getSysTime().getSystemTimestamp();
        if (DateUtil.isToday(inventoryNextTime, now)) {
            return StrFormatter.getChinesePrefixTime(inventoryNextTime);
        } else if (DateUtil.isTomorrow(inventoryNextTime, now)) {
            return "明天" + StrFormatter.getChinesePrefixTime(inventoryNextTime);
        } else if (DateUtil.isNextWeek(inventoryNextTime, now)) {
            return "下周" + DateUtil.getDayOfWeek(inventoryNextTime)
                    + StrFormatter.getChinesePrefixTime(inventoryNextTime);
        } else {
            return DateUtil.format(inventoryNextTime, "MM月d日") +
                    StrFormatter.getChinesePrefixTime(inventoryNextTime);
        }
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

    public long getExchangeNextTime() {
        return exchangeNextTime;
    }

    public void setExchangeNextTime(long exchangeNextTime) {
        this.exchangeNextTime = exchangeNextTime;
    }

    public long getInventoryNextTime() {
        return inventoryNextTime;
    }

    public void setInventoryNextTime(long inventoryNextTime) {
        this.inventoryNextTime = inventoryNextTime;
    }

    @Override
    public String toString() {
        return "ExchangeStatus{" +
                "exchangeId=" + exchangeId +
                ", nextTime='" + nextTime + '\'' +
                ", isCycle=" + isCycle +
                ", status=" + status +
                ", exchangeNextTime=" + exchangeNextTime +
                ", inventoryNextTime=" + inventoryNextTime +
                '}';
    }
}
