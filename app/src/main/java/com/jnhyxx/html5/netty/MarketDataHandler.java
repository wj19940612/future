package com.jnhyxx.html5.netty;

import com.jnhyxx.html5.domain.market.FullMarketData;

public abstract class MarketDataHandler<T> extends NettyHandler<T> {

    private String mContractCode;

    public MarketDataHandler(String contractCode) {
        mContractCode = contractCode;
    }

    @Override
    public void onReceiveData(T data) {
        if (data instanceof FullMarketData) {
            if (mContractCode.equalsIgnoreCase(((FullMarketData) data).getInstrumentId())) {
                onReceiveMarketData(data);
            }
        }
    }

    public abstract void onReceiveMarketData(T data);
}
