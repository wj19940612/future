package com.jnhyxx.html5.netty;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

public class NettyLoginFactory2 {

    private static final String TYPE_CODE_ID = "typeCodeId";
    private static final String TYPE = "type";

    public static JSONObject createRegisterJson(Integer id) throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(TYPE_CODE_ID, id);
        jsonObject.put(TYPE, 100);
        Log.d("TEST", "createRegisterJson: " + jsonObject.toString());
        return jsonObject;
    }
}
