package com.jnhyxx.html5.net;

import com.jnhyxx.html5.App;
import com.johnz.kutils.SecurityUtil;
import com.johnz.kutils.net.ApiParams;
import com.umeng.message.UmengRegistrar;

import java.security.NoSuchAlgorithmException;

public class API extends APIBase {

    public static final String TELE = "tele";
    public static final String CODE = "code";
    public static final String AUTH_CODE = "authCode";
    public static final String PASSWORD = "password";

    private API(String uri, ApiParams apiParams) {
        super(uri, apiParams);
    }

    public static class Account {
        /**
         * 更新友盟设备号 /user/user/updateUmCode
         * @param token
         */
        public static API updateUMDeviceId(String token) {
            ApiParams params = new ApiParams()
                    .put("token", token)
                    .put("umCode", UmengRegistrar.getRegistrationId(App.getAppContext()))
                    .put("platform", "android")
                    .put("environment", 1) // Release
                    .put("pkgtype", "androidcainiu");
            return new API("/user/user/updateUmCode", params);
        }

        /**
         * 获取注册短信验证码 /user/sms/getRegCode
         * @param tele
         */
        public static API obtainAuthCode(String tele) {
            String sign = null;
            try {
                 sign = SecurityUtil.md5Encrypt(tele + "luckin");
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }

            ApiParams params = new ApiParams()
                    .put(TELE, tele)
                    .put("sign", sign);
            return new API("/user/sms/getRegCode", params);
        }

        /**
         * 找回密码时候获取短信验证码 /user/sms/authLoginPwdCode
         * @param tele
         */
        public static API obtainAuthCodeWhenFindPwd(String tele) {
            ApiParams params = new ApiParams()
                    .put(TELE, tele);
            return new API("/user/sms/findLoginPwdCode", params);
        }

        public static API authCodeWhenFindPassword(String tele, String code) {
            ApiParams params = new ApiParams()
                    .put(TELE, tele)
                    .put(CODE, code);
            return new API("/user/sms/authLoginPwdCode", params);
        }

        /**
         * 注册 "/user/register
         * @param phoneNum
         * @param password
         * @param authCode
         */
        public static API signUp(String phoneNum, String password, String authCode) {
            ApiParams params = new ApiParams()
                    .put("tele", phoneNum)
                    .put(PASSWORD, password)
                    .put(AUTH_CODE, authCode);
            // TODO: 7/22/16 统计数据
                    /*.put("deviceModel", "deviceModel")
                    .put("deviceImei", "deviceImei")
                    .put("deviceVersion", "deviceVersion")
                    .put("clientVersion", "clientVersion")
                    .put("regSource", "regSource")
                    .put("operator", "operator");*/
            return new API("/user/register", params);
        }

        /**
         * 登录 /user/login
         * @param loginName
         * @param password
         */
        public static API signIn(String loginName, String password) {
            ApiParams params = new ApiParams()
                    .put("loginName", loginName)
                    .put(PASSWORD, password);
            // TODO: 7/22/16 统计数据
            /*      .put("deviceModel", deviceModel)
                    .put("deviceImei", deviceImei)
                    .put("deviceVersion", deviceVersion)
                    .put("clientVersion", clientVersion)
                    .put("operator", operator);*/
            return new API("/user/login", params);
        }

        /**
         * 找回登录密码-修改登录密码 /user/user/findLoginPwd
         * @param phone
         * @param authCode
         * @param newPwd
         */
        public static API modifyPwdWhenFindPwd(String phone, String authCode, String newPwd) {
            ApiParams params = new ApiParams()
                    .put(TELE, phone)
                    .put(AUTH_CODE, authCode)
                    .put(PASSWORD, newPwd);
            return new API("/user/user/findLoginPwd", params);
        }
    }

    public static class Finance {
        /**
         * 用户资金账户 /financy/financy/apiFinancyMain
         * @param token
         */
        public static API getFundInfo(String token) {
            ApiParams params = new ApiParams()
                    .put("token", token);
            return new API("/financy/financy/apiFinancyMain", params);
        }
    }
}
