package com.jnhyxx.html5.domain.order;

import android.text.TextUtils;

public class OrderReport {


    /**
     * nick : ***10
     * futuresType : 美原油
     * time : 15:46
     * tradeType : 做空
     */

    private String nick;
    private String futuresType;
    private String time;
    private String tradeType;

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public String getFuturesType() {
        return futuresType;
    }

    public void setFuturesType(String futuresType) {
        this.futuresType = futuresType;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTradeType() {
        return tradeType;
    }

    public void setTradeType(String tradeType) {
        this.tradeType = tradeType;
    }

    public boolean isShortSelling() {
        if (!TextUtils.isEmpty(getTradeType()) && getTradeType().equalsIgnoreCase("做空")) {
            return true;
        }
        return false;
    }
}
