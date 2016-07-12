package com.jnhyxx.html5.net;

import com.jnhyxx.html5.BuildConfig;

public class Api {

    public static String HOST = BuildConfig.API_HOST;

    //public static final String HOST0 = "http://www.jnhyxx.com";

    public static final String PATH_INDEX = "/index.html";
    public static final String PATH_MIME = "/mine.html";
    public static final String PATH_MESSAGE_DETAIL = "/news/detailed.html?id=";

    public static String getMainUrl() {
        return HOST + PATH_INDEX;
    }

    public static String getMime() {
        return HOST + PATH_MIME;
    }

    public static String getMessageDetial(String messageId) {
        return HOST + PATH_MESSAGE_DETAIL + messageId;
    }
}
