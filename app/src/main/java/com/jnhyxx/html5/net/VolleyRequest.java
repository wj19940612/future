package com.jnhyxx.html5.net;

import android.util.Log;

import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.jnhyxx.html5.R;
import com.jnhyxx.html5.utils.ToastUtil;
import com.umeng.message.proguard.T;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class VolleyRequest {

    private static VolleyRequest sInstance;

    private int method;
    private String url;
    private Map<String, Object> params;
    private JsonObject jsonObject;

    private Type type;
    private Class<T> clazz;
    private Map<String, String> headers;
    private Listener listener;
    private RetryPolicy retryPolicy;
    private String tag;
    private boolean errorToast;

    private GsonRequest<T> request;

    public static VolleyRequest with(String url) {
        sInstance = new VolleyRequest(url);
        return sInstance;
    }

    private VolleyRequest(String url) {
        this.url = url;
        this.method = Request.Method.GET;
        this.type = new TypeToken<String>() {}.getType();
    }

    /**
     * Put params for request
     * @param key
     * @param param
     * @return
     */
    public VolleyRequest put(String key, Object param) {
        if (params == null) {
            this.params = new HashMap<>();
        }
        this.params.put(key, param);
        return this;
    }

    /**
     * Put params for request
     * @param params
     * @return
     */
    public VolleyRequest put(Map<String, Object> params) {
        this.params = params;
        return this;
    }

    public VolleyRequest setType(Type type) {
        this.type = type;
        return this;
    }

    public VolleyRequest setErrorToast(boolean errorToast) {
        this.errorToast = errorToast;
        return this;
    }

    public VolleyRequest setClass(Class clazz) {
        this.clazz = clazz;
        return this;
    }

    public VolleyRequest setListener(Listener listener) {
        this.listener = listener;
        return this;
    }

    public VolleyRequest setRetryPolicy(RetryPolicy retryPolicy) {
        this.retryPolicy = retryPolicy;
        return this;
    }

    public VolleyRequest setHeader(String key, String value) {
        if (headers == null) {
            headers = new HashMap<>();
        }
        headers.put(key, value);
        return this;
    }

    public void post(String tag) {
        this.method = Request.Method.POST;
        get(tag);
    }

    public void get(String tag) {
        this.tag = tag;
        create();
        send();
    }

    public void post() {
        post(null);
    }

    public void get() {
        get(null);
    }

    private void create() {
        if (method < -1) throw new IllegalArgumentException("Method is illegal");
        if (listener == null) throw new IllegalArgumentException("Listener is null");
        // if (url == null) throw new IllegalArgumentException("Url is null");
        // if (headers == null) throw new IllegalArgumentException("Headers is null");
        // if (params == null) throw new IllegalArgumentException("Params is null");
        // if (jsonObject == null) throw new IllegalArgumentException("JsonObject is null");
        // if (type == null) throw new IllegalArgumentException("Type is all null");

        if (params != null) {
            if (method == Request.Method.GET) {
                url = mergeUrlAndParams(url, params);
                params = null;
            }
            createRequestBaseOnParams();
        } else if (jsonObject != null) {
            createRequestBaseOnJsonBody();
        } else {
            createRequestBaseOnNothing();
        }

        if (retryPolicy != null) {
            request.setRetryPolicy(retryPolicy);
        }

        if (tag != null) {
            request.setTag(tag);
        }
    }

    private void createRequestBaseOnParams() {
        if (clazz != null) {
            request = new GsonRequest(method, url, clazz, headers, params, listener, listener);
        } else {
            request = new GsonRequest(method, url, type, headers, params, listener, listener);
        }
    }

    private void createRequestBaseOnJsonBody() {
        if (clazz != null) {
            request = new GsonRequest(method, url, clazz, headers, jsonObject, listener, listener);
        } else {
            request = new GsonRequest(method, url, type, headers, jsonObject, listener, listener);
        }
    }

    private void createRequestBaseOnNothing() {
        if (clazz != null) {
            request = new GsonRequest(url, clazz, headers, listener, listener);
        } else {
            request = new GsonRequest(url, type, headers, listener, listener);
        }
    }

    private String mergeUrlAndParams(String url, Map<String, Object> params) {
        StringBuilder builder = new StringBuilder(url);
        if (params != null && !params.isEmpty()) {
            builder.append("?");
            for (Object key : params.keySet()) {
                builder.append(key).append("=").append(params.get(key)).append("&");
            }
            if (builder.toString().endsWith("&")) {
                builder.deleteCharAt(builder.length() - 1);
            }
        }
        return builder.toString();
    }

    private void send() {
        if (request != null) {

            if (listener != null) {
                listener.onStart();
                listener.setApi(url);
                listener.setErrorToast(errorToast);
            }

            RequestManager.executeRequest(request);
        }
    }

    public static class NullResponseError extends VolleyError {
        public NullResponseError(String exceptionMessage) {
            super(exceptionMessage);
        }
    }

    public static abstract class Listener<T> implements Response.Listener<T>, Response.ErrorListener {

        private boolean errorToast;
        private String api;

        public Listener() {
            this.errorToast = true;
        }

        public String getApi() {
            return api;
        }

        public void setApi(String api) {
            this.api = api;
        }

        public void setErrorToast(boolean errorToast) {
            this.errorToast = errorToast;
        }

        protected abstract void onSuccess(T t);

        protected void onStart() {}

        protected void onFinish() {}

        private void onReceive(T t) {
            if (t == null) {
                onFailure(new NullResponseError("Endpoint return null"));
            } else {
                onSuccess(t);
            }
        }

        @Override
        public void onResponse(T t) {
            onFinish();
            onReceive(t);
        }

        @Override
        public void onErrorResponse(VolleyError volleyError) {
            onFinish();
            onFailure(volleyError);
        }

        protected void onFailure(VolleyError volleyError) {
            Log.e(RequestManager.TAG,
                     "Error of " + getApi() + " --> "
                             + (volleyError != null ? volleyError.toString() : "Unknown"));

            if (volleyError instanceof NoConnectionError) {
                onConnectionError(volleyError);
            } else if (volleyError instanceof TimeoutError) {
                onTimeoutError(volleyError);
            } else if (volleyError instanceof ParseError) {
                onParseError(volleyError);
            } else if (volleyError instanceof ServerError) {
                onServerError(volleyError);
            } else if (volleyError instanceof NullResponseError) {

            }
        }

        private void toast(int msgId) {
            if (errorToast) ToastUtil.show(msgId);
        }

        protected void onConnectionError(VolleyError volleyError) {
            toast(R.string.network_error);
        }

        protected void onTimeoutError(VolleyError volleyError) {
            toast(R.string.network_time_out);
        }

        protected void onParseError(VolleyError volleyError) {
            toast(R.string.json_parse_error);
        }

        protected void onServerError(VolleyError volleyError) {
            toast(R.string.network_error);
        }
    }
}
