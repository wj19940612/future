package com.jnhyxx.html5.net;

import com.jnhyxx.html5.utils.ToastUtil;
import com.johnz.kutils.net.NullResponseError;

/**
 * 只在成功的时候（Resp's code = 200）时回调解析出 Resp's Data，并且判断 Data 是否为空，空处理类似 {@link NullResponseError} 的处理
 *
 * @param <T> Type of Resp
 * @param <D> Type of Data
 */
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
