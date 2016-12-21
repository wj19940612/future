package com.jnhyxx.html5.net;

import com.jnhyxx.html5.utils.ToastUtil;
import com.johnz.kutils.net.NullResponseError;

public abstract class Callback2<T, D> extends Callback<T> {

    @Override
    public void onReceive(T t) {
        if (t instanceof Resp) {
            Resp resp = (Resp) t;
            if (resp.isSuccess()) {
                D data = (D) resp.getData();
                if (data != null) {
                    onRespSuccess(data);
                } else {
                    onFailure(new NullResponseError(NullResponseError.RESP_DATA_NULL,
                            "Response's data is null"));
                }
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
