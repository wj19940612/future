package com.jnhyxx.html5.netty;

import org.json.JSONException;
import org.json.JSONObject;

public class NettyLoginFactory {

    private static final String CODE = "code";

    public static String getOpenSecret(String contractCode) throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(CODE, contractCode);
        return jsonObject.toString();
    }
}
