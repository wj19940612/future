package com.jnhyxx.html5.net;

import com.jnhyxx.html5.App;
import com.johnz.kutils.net.ApiParams;
import com.umeng.message.UmengRegistrar;

public class API extends APIBase {

    private static final String USER = "/user";
    private static final String ORDER = "/order";
    private static final String QUOTA = "/futuresquota";
    private static final String MARKET = "/market";
    private static final String FINANCE = "/financy";
    private static final String RULE = "/rule";
    private static final String SMS = "/sms";
    private static final String COTS = "/cots";

    /**
     * 更新友盟设备号
     */
    private static final String UPDATE_UMCODE = USER + "/user/updateUmCode";

    private API(String uri, ApiParams apiParams) {
        super(uri, apiParams);
    }

    public static class Account {

        public static API updateUMDeviceId(String token) {
            ApiParams params = new ApiParams()
                    .put("token", token)
                    .put("umCode", UmengRegistrar.getRegistrationId(App.getAppContext()))
                    .put("platform", "android")
                    .put("environment", 1) // Release
                    .put("pkgtype", "androidcainiu");
            return new API(UPDATE_UMCODE, params);
        }
    }

}
