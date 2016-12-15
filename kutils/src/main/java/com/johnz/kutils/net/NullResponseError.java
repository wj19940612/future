package com.johnz.kutils.net;

import com.android.volley.VolleyError;

public class NullResponseError extends VolleyError {
    public NullResponseError(String exceptionMessage) {
        super(exceptionMessage);
    }
}
