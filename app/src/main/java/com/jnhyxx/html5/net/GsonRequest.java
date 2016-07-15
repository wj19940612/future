package com.jnhyxx.html5.net;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class GsonRequest<T> extends Request<T> {

    private static final String PROTOCOL_CHARSET = "utf-8";
    private static final String PROTOCOL_CONTENT_TYPE = String.format("application/json; charset=%s", PROTOCOL_CHARSET);

    private final Map<String, String> headers;
    private final Listener<T> listener;
    private final JsonObject body;
    private final Map<String ,Object> params;
    private final Type type;
    private final Class<T> clazz;

    public GsonRequest(String url, Type type, Map<String, String> headers,
                       Response.Listener<T> listener, ErrorListener errorListener) {
        super(Method.GET, url, errorListener);
        this.type = type;
        this.headers = headers;
        this.listener = listener;
        this.body = null;
        this.params = null;
        this.clazz = null;
    }

    public GsonRequest(String url, Class<T> clazz, Map<String, String> headers,
                       Response.Listener<T> listener, ErrorListener errorListener) {
        super(Method.GET, url, errorListener);
        this.type = null;
        this.headers = headers;
        this.listener = listener;
        this.body = null;
        this.params = null;
        this.clazz = clazz;
    }

    public GsonRequest(int method, String url, Type type, Map<String, String> headers, JsonObject body,
                       Response.Listener<T> listener, ErrorListener errorListener) {
        super(method, url, errorListener);
        this.type = type;
        this.headers = headers;
        this.listener = listener;
        this.body = body;
        this.params = null;
        this.clazz = null;
    }

    public GsonRequest(int method, String url, Type type, Map<String, String> headers, Map<String, Object> params,
                       Response.Listener<T> listener, ErrorListener errorListener) {
        super(method, url, errorListener);
        this.type = type;
        this.headers = headers;
        this.listener = listener;
        this.body = null;
        this.params = params;
        this.clazz = null;
    }

    public GsonRequest(int method, String url, Class<T> clazz, Map<String, String> headers, JsonObject body,
                       Response.Listener<T> listener, ErrorListener errorListener) {
        super(method, url, errorListener);
        this.type = null;
        this.headers = headers;
        this.listener = listener;
        this.body = body;
        this.params = null;
        this.clazz = clazz;
    }

    public GsonRequest(int method, String url, Class<T> clazz, Map<String, String> headers, Map<String, Object> params,
                       Response.Listener<T> listener, ErrorListener errorListener) {
        super(method, url, errorListener);
        this.type = null;
        this.headers = headers;
        this.listener = listener;
        this.body = null;
        this.params = params;
        this.clazz = clazz;
    }


    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        if (params != null && !params.isEmpty()) {
            Map<String, String> map = new HashMap<>();
            for (Map.Entry<String, Object> entry: params.entrySet()) {
                map.put(entry.getKey(), entry.getValue().toString());
            }
            return map;
        }
        return super.getParams();
    }

/*
    @Override
    public byte[] getBody() throws AuthFailureError {
        try {
            return body != null ? body.toString().getBytes(PROTOCOL_CHARSET) : null;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }
*/

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        return headers != null ? headers : super.getHeaders();
    }

    @Override
    protected void deliverResponse(T response) {
        listener.onResponse(response);
    }
/*
    @Override
    public String getBodyContentType() {
        return PROTOCOL_CONTENT_TYPE;
    }*/

    @Override
    protected Response<T> parseNetworkResponse(NetworkResponse response) {
        String json;
        try {
            json = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
            T result;
            if (clazz != null) {
                result = new Gson().fromJson(json, clazz);
            } else {
                result = new Gson().fromJson(json, type);
            }
            return Response.success(result, HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch (JsonSyntaxException e) {
            return Response.error(new ParseError(e));
        }
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        switch (getMethod()) {
            case Request.Method.GET:
                builder.append("GET");
                break;
            case Request.Method.PUT:
                builder.append("PUT");
                break;
            case Request.Method.POST:
                builder.append("POST");
                break;
            case Request.Method.DELETE:
                builder.append("DELETE");
                break;
        }

        builder.append(" ").append(getUrl());
        buildParams(builder);
        buildBody(builder);
        buildHeader(builder);

        return builder.toString();
    }

    private void buildParams(StringBuilder builder) {
        if (params != null && !params.isEmpty()) {
            builder.append("?");
            for (Object key : params.keySet()) {
                builder.append(key).append("=")
                        .append(params.get(key)).append("&");
            }
            if (builder.toString().endsWith("&")) {
                builder.deleteCharAt(builder.length() - 1);
            }
        }
    }

    private void buildBody(StringBuilder builder) {
        if (body != null) {
            builder.append(" ").append(body.toString());
        }
    }

    private void buildHeader(StringBuilder builder) {
        if (headers != null) {
            for (Object key : headers.keySet()) {
                builder.append(" -H ").append('\'')
                        .append(key).append(": ")
                        .append(headers.get(key)).append('\'');
            }
        }
    }
}
