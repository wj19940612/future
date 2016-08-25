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
    public static final String SIGN = "sign";
    public static final String TOKEN = "token";
    public static final String PAGE_NO = "pageNo";
    public static final String PAGE_SIZE = "pageSize";
    public static final String TYPE = "type";
    public static final String FUND_TYPE = "fundType";
    public static final String FUTURES_TYPE = "futuresType";
    public static final String VERSION = "version";

    private API(String uri, ApiParams apiParams) {
        super(uri, apiParams);
    }

    public API(String uri, ApiParams apiParams, int version) {
        super(uri, apiParams, version);
    }

    public static class Account {
        /**
         * 更新友盟设备号 /user/user/updateUmCode
         *
         * @param token
         */
        public static API updateUMDeviceId(String token) {
            return new API("/user/user/updateUmCode",
                    new ApiParams()
                            .put("token", token)
                            .put("umCode", UmengRegistrar.getRegistrationId(App.getAppContext()))
                            .put("platform", "android")
                            .put("environment", 1) // Release
                            .put("pkgtype", "androidcainiu"));
        }

        /**
         * 获取注册短信验证码 /user/sms/getRegCode
         *
         * @param tele
         */
        public static API obtainAuthCode(String tele) {
            String sign = null;
            try {
                sign = SecurityUtil.md5Encrypt(tele + "luckin");
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }

            return new API("/user/sms/getRegCode",
                    new ApiParams()
                            .put(TELE, tele)
                            .put(SIGN, sign));
        }

        /**
         * 找回密码时候获取短信验证码 /user/sms/findLoginPwdCode
         *
         * @param tele
         */
        public static API obtainAuthCodeWhenFindPwd(String tele) {
            String sign = null;
            try {
                sign = SecurityUtil.md5Encrypt(tele + "luckin");
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }

            return new API("/user/sms/findLoginPwdCode",
                    new ApiParams()
                            .put(TELE, tele)
                            .put(SIGN, sign));
        }

        /**
         * 找回登录密码 - 验证码验证 /user/sms/authLoginPwdCode
         *
         * @param tele
         * @param code
         * @return
         */
        public static API authCodeWhenFindPassword(String tele, String code) {
            return new API("/user/sms/authLoginPwdCode",
                    new ApiParams()
                            .put(TELE, tele)
                            .put(CODE, code));
        }

        /**
         * 注册 /user/register.do
         *
         * @param phoneNum
         * @param password
         * @param authCode
         */
        public static API signUp(String phoneNum, String password, String authCode) {
            ApiParams params = new ApiParams()
                    .put("userPhone", phoneNum)
                    .put("userPass", password)
                    .put("regCode", authCode);
            // TODO: 7/22/16 统计数据
                    /*.put("deviceModel", "deviceModel")
                    .put("deviceImei", "deviceImei")
                    .put("deviceVersion", "deviceVersion")
                    .put("clientVersion", "clientVersion")
                    .put("regSource", "regSource")
                    .put("operator", "operator");*/
            return new API("/user/register.do", params);
        }

        /**
         * 登录 /user/login
         *
         * @param loginName
         * @param password
         */
        public static API signIn(String loginName, String password) {
            try {
                password = SecurityUtil.md5Encrypt(password);
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }

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
         *
         * @param phone
         * @param authCode
         * @param newPwd
         */
        public static API modifyPwdWhenFindPwd(String phone, String authCode, String newPwd) {
            return new API("/user/user/findLoginPwd",
                    new ApiParams()
                            .put(TELE, phone)
                            .put(AUTH_CODE, authCode)
                            .put(PASSWORD, newPwd));
        }

        /**
         * 验证银行卡是否绑定 /user/user/checkBankCard
         *
         * @param token
         */
        public static API getBankcardInfo(String token) {
            return new API("/user/user/checkBankCard",
                    new ApiParams()
                            .put(TOKEN, token));
        }


        /**
         * 获取简单的个人信息 /user/user/getAcountDetail
         *
         * @param token
         * @return
         */
        public static API getProfileSummary(String token) {
            return new API("/user/user/getAcountDetail",
                    new ApiParams()
                            .put(TOKEN, token));
        }

        /**
         * /user/user/checkUserName 验证是否实名认证
         *
         * @param token
         * @return
         */
        public static API getUserNameAuth(String token) {
            return new API("/user/user/checkUserName",
                    new ApiParams()
                            .put(TOKEN, token));
        }

        /**
         * /user/user/authUser 实名认证
         *
         * @param token
         * @param realName
         * @param identityNum
         * @return
         */
        public static API authUserName(String token, String realName, String identityNum) {
            return new API("/user/user/authUser",
                    new ApiParams()
                            .put(TOKEN, token)
                            .put("realName", realName)
                            .put("idCard", identityNum));
        }

        /**
         * /user/user/updatebank 认证银行卡
         *
         * @param token
         * @param bankcardNum
         * @param bankName
         * @param phoneNum
         * @return
         */
        public static API updateBankcard(String token, String bankcardNum, String bankName, String phoneNum) {
            return new API("/user/user/updatebank",
                    new ApiParams()
                            .put(TOKEN, token).put("bankNum", bankcardNum)
                            .put("bankName", bankName)
                            .put("phone", phoneNum));
        }

        /**
         * /user/newsArticle/newsList 获取资讯: sectionId: 行情分析-58, 行业资讯-57
         *
         * @param token
         * @param sectionId
         * @param pageNo
         * @param pageSize
         * @return
         */
        public static API getInfo(String token, int sectionId, int pageNo, int pageSize) {
            return new API("/user/newsArticle/newsList",
                    new ApiParams()
                            .put(TOKEN, token)
                            .put("sectionId", sectionId)
                            .put(PAGE_NO, pageNo)
                            .put(PAGE_SIZE, pageSize));
        }

        /**
         * /user/newsNotice/newsImgList 获取首页广告
         *
         * @return
         */
        public static API getHomeAdvertisements() {
            return new API("/user/newsNotice/newsImgList",
                    new ApiParams()
                            .put(TYPE, 2));
        }
    }

    public static class Finance {
        /**
         * 用户资金账户 /financy/financy/apiFinancyMain
         *
         * @param token
         */
        public static API getFundInfo(String token) {
            return new API("/financy/financy/apiFinancyMain",
                    new ApiParams()
                            .put("token", token));
        }

        /**
         * 用户提现申请 /financy/financy/apiWithdraw
         *
         * @param token
         * @param amount
         */
        public static API withdraw(String token, double amount) {
            return new API("/financy/financy/apiWithdraw",
                    new ApiParams()
                            .put(TOKEN, token)
                            .put("inoutAmt", amount));
        }

        /**
         * /financy/financy/apiFinancyFlowList 资金(现金)流水列表
         *
         * @param token
         * @param pageNo
         * @param pageSize
         * @return
         */
        public static API getCashFlowList(String token, int pageNo, int pageSize) {
            return new API("/financy/financy/apiFinancyFlowList",
                    new ApiParams()
                            .put(TOKEN, token)
                            .put(PAGE_NO, pageNo)
                            .put(PAGE_SIZE, pageSize));
        }

        /**
         * /financy/financy/apiScoreFinancyFlowList 积分流水列表
         *
         * @param token
         * @param pageNo
         * @param pageSize
         * @return
         */
        public static API getScoreFlowList(String token, int pageNo, int pageSize) {
            return new API("/financy/financy/apiScoreFinancyFlowList",
                    new ApiParams()
                            .put(TOKEN, token)
                            .put(PAGE_NO, pageNo)
                            .put(PAGE_SIZE, pageSize));
        }
    }

    public static class Message {
        /**
         * /sms/message/systemMessages 系统消息列表
         *
         * @param token
         * @return
         */
        public static API getSystemMessageList(String token, int pageNo, int pageSize) {
            return new API("/sms/message/systemMessages",
                    new ApiParams()
                            .put(TOKEN, token)
                            .put(PAGE_NO, pageNo)
                            .put(PAGE_SIZE, pageSize));
        }

        /**
         * /sms/message/traderMassages 交易提醒列表
         *
         * @param token
         * @return
         */
        public static API getTradeMessageList(String token, int pageNo, int pageSize) {
            return new API("/sms/message/traderMassages",
                    new ApiParams()
                            .put(TOKEN, token)
                            .put(PAGE_NO, pageNo)
                            .put(PAGE_SIZE, pageSize));
        }
    }

    public static class Market {

        /**
         * /market/futureCommodity/select 获取首页产品列表
         * /order/variety/getVariety.do
         *
         * @return
         */
        public static API getProductList() {
            return new API("/order/variety/getVariety.do", null);
        }

        /**
         * /futuresquota/getAllCacheData 获取产品行情的简要
         *
         * @return
         */
        public static API getProductMarketBriefList() {
            return new API("/futuresquota/getAllCacheData", null);
        }
    }

    public static class Order {

        /**
         * /order/posiOrderCount 获取用户每个产品订单持仓情况的简要
         *
         * @param token
         * @return
         */
        public static API getOrderPositionList(String token) {
            return new API("/order/posiOrderCount",
                    new ApiParams()
                            .put(TOKEN, token)
                            .put(VERSION, "0.0.1"));
        }

        /**
         * /order/order/indexReport 获取持仓播报数据
         *
         * @return
         */
        public static API getReportData() {
            return new API("/order/order/indexReport", null);
        }

        /**
         * /order/futures/balancedList 获取结算订单列表
         *
         * @param token
         * @param pageNo
         * @param pageSize
         * @param id
         * @param fundType @return
         */
        public static API getSettlementOrderList(String token, int pageNo, int pageSize, int id, int fundType) {
            return new API("/order/futures/balancedList",
                    new ApiParams()
                            .put(TOKEN, token)
                            .put(PAGE_NO, pageNo)
                            .put(PAGE_SIZE, pageSize)
                            .put(FUTURES_TYPE, id)
                            .put(FUND_TYPE, fundType));
        }
    }
}
