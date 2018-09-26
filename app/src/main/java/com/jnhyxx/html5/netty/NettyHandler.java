package com.jnhyxx.html5.netty;

import android.os.Handler;
import android.os.Message;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.internal.$Gson$Types;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public abstract class NettyHandler<T> extends Handler {

    public static final int WHAT_ERROR = 0;
    public static final int WHAT_DATA = 1;

    public abstract void onReceiveData(T data);

    protected void onError(String message) {
    }

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        switch (msg.what) {
            case WHAT_ERROR:
                onError((String) msg.obj);
                break;
            case WHAT_DATA:
                String originalData = (String) msg.obj;
                try {
                    T result = new Gson().fromJson(originalData, getGenericType());
                    onReceiveData(result);
                } catch (JsonSyntaxException e) {
                    onError(e.getMessage());
                }
                break;
        }
    }

    public Type getGenericType() {
        Type superclass = getClass().getGenericSuperclass();
        if (superclass instanceof Class) {
            throw new RuntimeException("Missing type parameter.");
        }
        ParameterizedType parameterizedType = (ParameterizedType) superclass;
        return $Gson$Types.canonicalize(parameterizedType.getActualTypeArguments()[0]);
    }
}
