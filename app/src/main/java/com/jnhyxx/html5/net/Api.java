package com.jnhyxx.html5.net;

import android.util.Log;

import com.jnhyxx.html5.App;
import com.jnhyxx.html5.BuildConfig;
import com.jnhyxx.html5.utils.NotificationUtil;
import com.umeng.message.UmengRegistrar;

public class Api {

    public static String HOST = BuildConfig.API_HOST;

    //public static final String HOST0 = "http://www.jnhyxx.com";

    public static final String PATH_INDEX = "/index.html";
    public static final String PATH_MIME = "/mine.html";
    public static final String PATH_MESSAGE_DETAIL = "/news/detailed.html?id=";
    public static final String PATH_MESSAGE_LIST = "/news/Message.html";

    public static final String API_UPDATE_UMCODE = "/user/user/updateUmCode";		//更新友盟设备号

    public static String getMainUrl() {
        return HOST + PATH_INDEX;
    }

    public static String getMime() {
        return HOST + PATH_MIME;
    }

    public static String getMessageDetail(String messageId) {
        return HOST + PATH_MESSAGE_DETAIL + messageId;
    }

    public static String getMessageList(String messageType) {
        if (messageType.equals(NotificationUtil.MESSAGE_TYPE_SYSTEM)) {
            return HOST + PATH_MESSAGE_LIST;
        } else if (messageType.equals(NotificationUtil.MESSAGE_TYPE_TRADE)) {
            return HOST + PATH_MESSAGE_LIST + "?key=trade";
        }
        return HOST + PATH_MESSAGE_LIST;
    }

    public static void updateUMDeviceId(String token) {
        VolleyRequest.with(HOST + API_UPDATE_UMCODE)
                .put("token", token)
                .put("umCode", UmengRegistrar.getRegistrationId(App.getAppContext()))
                .put("platform", "android")
                .put("environment", 1) // Release
                .put("pkgtype", "androidcainiu")
                .setErrorToast(true)
                .setClass(Response.class)
                .setListener(new VolleyRequest.Listener() {
                    @Override
                    protected void onSuccess(Object o) {
                        Log.d("Request", "onSuccess: " + o);
                    }
                }).post();
    }

    private class Response<T> {

        private int code;
        private String msg;
        private Integer msgType;
        private String errparam;
        private T data;

        public Integer getCode() {
            return code;
        }

        public String getMsg() {
            return msg;
        }

        public void setMsg(){
            msg = "";
        }

        public Integer getMsgType() {
            return msgType;
        }

        public String getErrparam() {
            return errparam;
        }

        public T getData() {
            return data;
        }

        public boolean isSuccess() {
            return code == 200;
        }

        public boolean hasData() {
            return data != null;
        }

        @Override
        public String toString() {
            return "Response{" +
                    "code=" + code +
                    ", msg='" + msg + '\'' +
                    ", errparam='" + errparam + '\'' +
                    ", data=" + data +
                    '}';
        }
    }


}
