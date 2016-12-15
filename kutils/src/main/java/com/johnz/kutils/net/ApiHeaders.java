package com.johnz.kutils.net;

import java.util.HashMap;

public class ApiHeaders {

    private HashMap<String, String> mHeaders;

    public ApiHeaders put(String key, Object value) {
        if (mHeaders == null) {
            mHeaders = new HashMap<>();
        }

        if (value != null) {
            mHeaders.put(key, value.toString());
        }

        return this;
    }

    public HashMap<String, String> get() {
        return mHeaders;
    }
}
