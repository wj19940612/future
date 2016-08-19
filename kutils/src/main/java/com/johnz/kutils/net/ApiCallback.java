package com.johnz.kutils.net;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.internal.$Gson$Types;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 *
 * @param <T>
 */
public abstract class ApiCallback<T> implements Response.Listener<T>, Response.ErrorListener {

    public interface onFinishedListener {
        void onFinished(String url);
    }

    private String mUrl;
    private onFinishedListener mOnFinishedListener;
    private String mTag;
    private ApiIndeterminate mIndeterminate;

    public void setUrl(String url) {
        mUrl = url;
    }

    public void setOnFinishedListener(onFinishedListener onFinishedListener) {
        mOnFinishedListener = onFinishedListener;
    }

    public void setTag(String tag) {
        mTag = tag;
    }

    public String getTag() {
        return mTag;
    }

    public String getUrl() {
        return mUrl;
    }

    public void setIndeterminate(ApiIndeterminate Indeterminate) {
        mIndeterminate = Indeterminate;
    }

    public void onStart() {
        if (mIndeterminate != null) {
            mIndeterminate.onShow(mTag);
        }
    }

    public void onFinish() {
        if (mOnFinishedListener != null) {
            mOnFinishedListener.onFinished(mUrl);
        }

        if (mIndeterminate != null) {
            mIndeterminate.onDismiss(mTag);
        }
    }

    public abstract void onSuccess(T t);

    public abstract void onFailure(VolleyError volleyError);

    @Override
    public void onErrorResponse(VolleyError volleyError) {
        onFailure(volleyError);
        onFinish();
    }

    @Override
    public void onResponse(T t) {
        if (t != null) {
            onSuccess(t);
        } else {
            onFailure(new NullResponseError("Server return null"));
        }
        onFinish();
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
