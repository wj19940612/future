package com.jnhyxx.chart.domain;

public class FlashViewData {

    private float lastPrice;

    public FlashViewData(float lastPrice) {
        this.lastPrice = lastPrice;
    }

    public float getLastPrice() {
        return lastPrice;
    }
}
