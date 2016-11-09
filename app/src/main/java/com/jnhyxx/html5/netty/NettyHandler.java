package com.jnhyxx.html5.netty;

import android.os.Handler;
import android.os.Message;

import com.jnhyxx.html5.domain.market.FullMarketData;

public abstract class NettyHandler extends Handler {

    public static final int WHAT_ERROR = 0;
    public static final int WHAT_DATA = 1;
    public static final int WHAT_ORIGINAL = 2;

    protected void onReceiveData(FullMarketData data){
    }

    protected void onError(String message) {
    }

    protected void onReceiveOriginalData(String data) {
    }

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        switch (msg.what) {
            case WHAT_ERROR:
                onError((String) msg.obj);
                break;
            case WHAT_DATA:
                onReceiveData((FullMarketData) msg.obj);
                break;
            case WHAT_ORIGINAL:
                onReceiveOriginalData((String) msg.obj);
                break;
        }
    }
}
