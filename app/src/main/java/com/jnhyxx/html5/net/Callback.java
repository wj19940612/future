package com.jnhyxx.html5.net;

import android.util.Log;

import com.android.volley.NetworkError;
import com.android.volley.ParseError;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.jnhyxx.html5.R;
import com.jnhyxx.html5.utils.ToastUtil;
import com.johnz.kutils.net.ApiCallback;
import com.johnz.kutils.net.NullResponseError;

public abstract class Callback<T> extends ApiCallback<T> {

    @Override
    public void onSuccess(T t) {
        Log.d(getTag(), "onSuccess: " + t.toString());
        onReceive(t);
    }

    @Override
    public void onFailure(VolleyError volleyError) {
        Log.d("wj", volleyError.toString());
        int toastResId = R.string.api_error_network;
        if (volleyError instanceof NullResponseError) {
            toastResId = R.string.api_error_null;
        } else if (volleyError instanceof TimeoutError) {
            toastResId = R.string.api_error_timeout;
        } else if (volleyError instanceof ParseError) {
            toastResId = R.string.api_error_parse;
        } else if (volleyError instanceof NetworkError) {
            toastResId = R.string.api_error_network;
        } else if (volleyError instanceof ServerError) {
            toastResId = R.string.api_error_server;
        }
        ToastUtil.show(toastResId);
    }

    public abstract void onReceive(T t);
}
