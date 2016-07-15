package com.jnhyxx.html5.net;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class RequestManager {

    public static final String TAG = "Request";

    private static RequestQueue sRequestQueue;

    public static void init(Context context) {
        sRequestQueue = Volley.newRequestQueue(context);
    }

    public static void executeRequest(Request<?> request) {
        print(request);
        if (sRequestQueue != null) {
            sRequestQueue.add(request);
        } else {
            throw new NullPointerException("Request queue isn't initialized.");
        }
    }

    public static void cancelRequest(String tag) {
        if (sRequestQueue != null) {
            sRequestQueue.cancelAll(tag);
        }
    }

    private static void print(Request request) {
        if (request != null) {
            Log.d(TAG, request.toString());
        }
    }
}
