package com.jnhyxx.html5.net;

import android.text.TextUtils;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.jnhyxx.html5.BuildConfig;
import com.johnz.kutils.net.ApiCallback;
import com.johnz.kutils.net.ApiHeaders;
import com.johnz.kutils.net.ApiIndeterminate;
import com.johnz.kutils.net.ApiParams;
import com.johnz.kutils.net.CookieManger;
import com.johnz.kutils.net.GsonRequest;
import com.johnz.kutils.net.RequestManager;

import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Set;

public class APIBase extends RequestManager {

    private final static String HOST = BuildConfig.API_HOST;
    private static String mHost;

    private static Set<String> sCurrentUrls = new HashSet<>();

    private int mMethod;
    private String mTag;
    private String mUri;
    private ApiCallback<?> mCallback;
    private ApiParams mApiParams;
    private ApiIndeterminate mIndeterminate;
    private RetryPolicy mRetryPolicy;

    protected APIBase(String uri, ApiParams apiParams) {
        this(Request.Method.POST, uri, apiParams, 0);
    }

    protected APIBase(String uri, ApiParams apiParams, int version) {
        this(Request.Method.POST, uri, apiParams, version);
    }

    protected APIBase(int method, String uri, ApiParams apiParams) {
        this(method, uri, apiParams, 0);
    }

    protected APIBase(int method, String uri, ApiParams apiParams, int version) {
        mUri = uri;
        mApiParams = apiParams;
        mMethod = method;
        mTag = "";
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

    public APIBase setRetryPolicy(RetryPolicy policy) {
        this.mRetryPolicy = policy;
        return this;
    }

    public static String getHost() {
        if (TextUtils.isEmpty(mHost)) {
            mHost = HOST;
        }
        return mHost;
    }

    public static void setHost(String host) {
        mHost = host;
    }

    public void fire() {
        synchronized (sCurrentUrls) {
            String url = createUrl();

            if (sCurrentUrls.add(url)) {
                createThenEnqueue(url);
            }
        }
    }

    public void fireSync() {
        String url = createUrl();

        createThenEnqueue(url);
    }

    private String createUrl() {
        String url = new StringBuilder(getHost()).append(mUri).toString();
        if (mMethod == Request.Method.GET && mApiParams != null) {
            url = url + mApiParams.toString();
            mApiParams = null;
        }
        return url;
    }

    private void createThenEnqueue(String url) {
        ApiHeaders headers = new ApiHeaders();
        String cookies = CookieManger.getInstance().getCookies();
        if (!TextUtils.isEmpty(cookies)) {
            headers.put("Cookie", cookies);
        }

        Type type;
        if (mCallback != null) {
            mCallback.setUrl(url);
            mCallback.setOnFinishedListener(new RequestFinishedListener());
            mCallback.setTag(mTag);
            mCallback.setIndeterminate(mIndeterminate);
            mCallback.onStart();
            type = mCallback.getGenericType();

        } else { // create a default callback for handle request finish event
            mCallback = new ApiCallback<Object>() {
                @Override
                public void onSuccess(Object o) {
                    Log.d(TAG, "onReceive: result(default): " + o);
                }

                @Override
                public void onFailure(VolleyError volleyError) {
                    Log.d(TAG, "onFailure: error(default): " +
                            volleyError == null ? null : volleyError.toString());
                }
            };
            mCallback.setUrl(url);
            mCallback.setOnFinishedListener(new RequestFinishedListener());
            type = mCallback.getGenericType();
        }

        GsonRequest request = new GsonRequest(mMethod, url, headers, mApiParams, type, mCallback);
        request.setTag(mTag);

        if (mRetryPolicy != null) {
            request.setRetryPolicy(mRetryPolicy);
        }

        enqueue(request);
    }

    private static class RequestFinishedListener implements ApiCallback.onFinishedListener {

        public void onFinished(String url) {
            if (sCurrentUrls != null) {
                Log.d(TAG, "onFinished: " + url);
                sCurrentUrls.remove(url);
            }
        }
    }
}
