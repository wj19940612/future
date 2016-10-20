package com.jnhyxx.html5.net;

import com.jnhyxx.html5.utils.ToastUtil;

public abstract class Callback2<T, D> extends Callback<T> {

    @Override
    public void onReceive(T t) {
        if (t instanceof Resp) {
            if (((Resp) t).isSuccess()) {
                onRespSuccess((D) ((Resp) t).getData());
            } else {
                onErrorMessageShow(((Resp) t).getMsg());
            }
        }
    }

    private void onErrorMessageShow(String msg) {
        if (getErrorVisible()) {
            ToastUtil.show(msg);
        }
    }

    public abstract void onRespSuccess(D d);

}
