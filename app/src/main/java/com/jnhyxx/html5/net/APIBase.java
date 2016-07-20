package com.jnhyxx.html5.net;

import android.util.Log;

import com.android.volley.VolleyError;
import com.jnhyxx.html5.BuildConfig;
import com.johnz.kutils.net.ApiCallback;
import com.johnz.kutils.net.ApiIndeterminate;
import com.johnz.kutils.net.ApiParams;
import com.johnz.kutils.net.GsonRequest;
import com.johnz.kutils.net.RequestManager;

import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Set;

public class APIBase extends RequestManager {

    public final static String HOST = BuildConfig.API_HOST;

    private static Set<String> sCurrentUrls = new HashSet<>();

    private String mTag;
    private String mUri;
    private ApiCallback<?> mCallback;
    private ApiParams mApiParams;
    private ApiIndeterminate mIndeterminate;

    protected APIBase(String uri, ApiParams apiParams) {
        mUri = uri;
        mApiParams = apiParams;
    }

    public APIBase setTag(String tag) {
        this.mTag = tag;
        return this;
    }

    public APIBase setIndeterminate(ApiIndeterminate apiProgress) {
        mIndeterminate = apiProgress;
        return this;
    }

    public APIBase setCallback(ApiCallback<?> callback) {
        this.mCallback = callback;
        return this;
    }

    public void post() {
        synchronized (sCurrentUrls) {
            String url = new StringBuilder(HOST).append(mUri).toString();

            if (sCurrentUrls.add(url) || true) {
                Type type = null;

                if (mCallback != null) {
                    mCallback.setUrl(url);
                    mCallback.setOnFinishedListener(new RequestFinishedListener());
                    mCallback.setTag(mTag);
                    mCallback.setApiProgress(mIndeterminate);
                    mCallback.onStart();
                    type = mCallback.getGenericType();

                } else { // create a default callback for handle request finish event
                    mCallback = new ApiCallback<Object>() {
                        @Override
                        public void onSuccess(Object o) {
                            Log.d(TAG, "onSuccess: result(default): " + o);
                        }
                        @Override
                        public void onFailure(VolleyError volleyError) {
                            Log.d(TAG, "onFailure: error(default): " +
                                    volleyError == null? null: volleyError.toString());
                        }
                    };
                    mCallback.setUrl(url);
                    mCallback.setOnFinishedListener(new RequestFinishedListener());
                    type = mCallback.getGenericType();
                }

                GsonRequest request = new GsonRequest(url, mApiParams, type, mCallback);
                request.setTag(mTag);

                enqueue(request);
            }
        }
    }

    private static class RequestFinishedListener implements ApiCallback.onFinishedListener {

        public void onFinished(String url) {
            if (sCurrentUrls != null) {
                Log.d(TAG, "onFinished: " + url);
                sCurrentUrls.remove(url);
            }
        }
    }

    public class Response<T> {

        private int code;
        private String msg;
        private Integer msgType;
        private String errparam;
        private T data;

        public Integer getCode() {
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
}
