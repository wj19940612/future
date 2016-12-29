package com.jnhyxx.html5.net;

import com.jnhyxx.html5.utils.ToastUtil;

/**
 * 只在成功的时候（Resp's code = 200）时回调解析出 Resp
 *
 * @param <T> Type of Resp
 */
public abstract class Callback1<T> extends Callback<T> {

    public Callback1(boolean errorVisible) {
        super(errorVisible);
    }

    public Callback1() {
        super();
    }

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

    protected void onErrorMessageShow(String msg) {
        if (getErrorVisible()) {
            ToastUtil.show(msg);
        }
    }


    protected abstract void onRespSuccess(T resp);
}
