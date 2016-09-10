package com.jnhyxx.html5.netty;

import android.os.Handler;
import android.os.Message;

import com.luckin.magnifier.model.newmodel.Strategy;
import com.luckin.magnifier.model.newmodel.futures.FuturesQuotaData;

public abstract class NettyHandler extends Handler {

    public static final int WHAT_ERROR = 0;
    public static final int WHAT_SINGLE_DATA = 1;
    public static final int WHAT_STRATEGY = 2;

    protected void onReceiveSingleData(FuturesQuotaData data) {
    }

    protected void onReceiveStrategy(Strategy strategy) {
    }

    protected void onError(String message) {
    }

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        switch (msg.what) {
            case WHAT_ERROR:
                onError((String) msg.obj);
                break;
            case WHAT_SINGLE_DATA:
                onReceiveSingleData((FuturesQuotaData) msg.obj);
                break;
            case WHAT_STRATEGY:
                onReceiveStrategy((Strategy) msg.obj);
                break;
        }
    }
}
