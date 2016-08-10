package com.jnhyxx.html5.net;


import com.jnhyxx.html5.utils.ToastUtil;

public class Resp<T> {

    private int code;
    private String msg;
    private int msgType;
    private String errparam;
    private T data;

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(){
        msg = "";
    }

    public Integer getMsgType() {
        return msgType;
    }

    public String getErrparam() {
        return errparam;
    }

    public T getData() {
        return data;
    }

    public boolean isSuccess() {
        return code == 200;
    }

    public boolean hasData() {
        return data != null;
    }

    @Override
    public String toString() {
        return "Response{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                ", errparam='" + errparam + '\'' +
                ", data=" + data +
                '}';
    }

    public abstract static class Callback<T, D> extends com.jnhyxx.html5.net.Callback<T> {

        @Override
        public void onReceive(T t) {
            if (t instanceof Resp) {
                if (((Resp) t).isSuccess()) {
                    onRespReceive((D) ((Resp) t).getData());
                } else {
                    ToastUtil.show(((Resp) t).getMsg());
                }
            }
        }

        public abstract void onRespReceive(D d);
    }
}
