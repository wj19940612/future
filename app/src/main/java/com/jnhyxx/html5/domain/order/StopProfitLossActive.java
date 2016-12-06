package com.jnhyxx.html5.domain.order;

/**
 * 二次设置止盈止损配置对象
 */
public class StopProfitLossActive {

    /**
     * active : 1
     * lossOffsetBeat : 1
     * winOffsetBeat : 1
     */

    private String active;
    private int lossOffsetBeat;
    private int winOffsetBeat;

    public String getActive() {
        return active;
    }

    public boolean isActive() {
        return active.equalsIgnoreCase("Y");
    }
}
