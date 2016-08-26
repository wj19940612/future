package com.johnz.kutils.net;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.util.Map;

public class GsonRequest<T> extends Request<T> {

    private final Map<String, String> headers;
    private final Map<String, String> params;
    private final Type type;
    private final Listener<T> listener;

    public GsonRequest(int method, String url, ApiParams params, Type type, ApiCallback<T> callback) {
        super(method, url, callback);
        this.headers = null;
        this.params = params != null ? params.get() : null;
        this.type = type;
        this.listener = callback;
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        if (params != null && !params.isEmpty()) {
            return params;
        }
        return super.getParams();
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        return headers != null ? headers : super.getHeaders();
    }

    @Override
    protected void deliverResponse(T response) {
        listener.onResponse(response);
    }

    @Override
    protected Response<T> parseNetworkResponse(NetworkResponse response) {
        String json;
        try {
            json = new String(response.data, HttpHeaderParser.parseCharset(response.headers));

            CookieManger.getInstance().parse(response.headers);

            T result = new Gson().fromJson(json, type);

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
