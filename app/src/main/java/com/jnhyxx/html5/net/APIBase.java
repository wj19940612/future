package com.jnhyxx.html5.net;

import android.text.TextUtils;
import android.util.Log;

import com.android.volley.Request;
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

import static android.content.ContentValues.TAG;

public class APIBase extends RequestManager {

    public final static String HOST = BuildConfig.API_HOST;

    private static Set<String> sCurrentUrls = new HashSet<>();

    private int mMethod;
    private String mTag;
    private String mUri;
    private String mHost;
    private ApiCallback<?> mCallback;
    private ApiParams mApiParams;
    private ApiIndeterminate mIndeterminate;

    protected APIBase(String uri, ApiParams apiParams) {
        this(uri, apiParams, 0);
    }

    protected APIBase(String uri, ApiParams apiParams, int version) {
        mUri = uri;
        mApiParams = apiParams;
        mMethod = Request.Method.POST;
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


    public void get() {
        mMethod = Request.Method.GET;
        post();
    }

    public void post() {
        synchronized (sCurrentUrls) {
            if (TextUtils.isEmpty(mHost)) mHost = HOST;
            String url = new StringBuilder(mHost).append(mUri).toString();

            if (sCurrentUrls.add(url)) {
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

                GsonRequest request = new GsonRequest(mMethod, url, mApiParams, type, mCallback);
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
}
