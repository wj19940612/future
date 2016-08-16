package com.jnhyxx.html5.net;

import com.jnhyxx.html5.utils.ToastUtil;

public abstract class Callback1<T> extends Callback<T> {

    @Override
    public void onReceive(T t) {
        if (t instanceof Resp) {
            if (((Resp) t).isSuccess()) {
                onRespSuccess(t);
            } else {
                onErrorMessageShow(((Resp) t).getMsg());
            }
        }
    }

    private void onErrorMessageShow(String msg) {
        ToastUtil.show(msg);
    }


    protected abstract void onRespSuccess(T resp);
}
