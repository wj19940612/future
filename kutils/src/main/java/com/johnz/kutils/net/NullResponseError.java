package com.johnz.kutils.net;

import com.android.volley.VolleyError;

public class NullResponseError extends VolleyError {

    public static final int RESP_DATA_NULL = 1;

    private int type;

    public NullResponseError(String exceptionMessage) {
        super(exceptionMessage);
        type = 0;
    }

    public NullResponseError(int t, String exceptionMessage) {
        super(exceptionMessage);
        type = t;
    }

    public boolean isRespDataNull() {
        return type == RESP_DATA_NULL;
    }
}
