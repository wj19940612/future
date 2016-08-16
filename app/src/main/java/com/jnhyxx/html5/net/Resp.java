package com.jnhyxx.html5.net;


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
}
