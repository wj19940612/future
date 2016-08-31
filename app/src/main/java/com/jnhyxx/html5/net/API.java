package com.jnhyxx.html5.net;

import com.android.volley.Request;
import com.jnhyxx.html5.App;
import com.jnhyxx.html5.BuildConfig;
import com.johnz.kutils.SecurityUtil;
import com.johnz.kutils.net.ApiParams;
import com.umeng.message.UmengRegistrar;

import java.security.NoSuchAlgorithmException;

public class API extends APIBase {

    private static final int GET = Request.Method.GET;

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

    public API(int method, String uri, ApiParams apiParams) {
        super(method, uri, apiParams);
    }

    public API(int method, String uri, ApiParams apiParams, int version) {
        super(method, uri, apiParams, version);
    }

    public static class User {

        /**
         * /user/user/getSystemTime.do 获取服务器系统时间,用于同步
         *
         * @return
         */
        public static API getSystemTime() {
            return new API("/user/user/getSystemTime.do", null);
        }

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
            // TODO: 2016/8/30 原来的获取验证码接口
//            return new API("/user/sms/getRegCode",
//                    new ApiParams()
//                            .put(TELE, tele)
//                            .put(SIGN, sign));
            return new API("/user/user/getRegCode.do",
                    new ApiParams()
                            .put("userPhone", tele)
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

            return new API("/user/user/retrievePass.do",
                    new ApiParams()
                            .put("userPhone", tele));
//                            .put(SIGN, sign));
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
         * @param regCode
         */
        public static API signUp(String phoneNum, String password, String regCode, String promoterCode) {
            return new API("/user/register.do",
                    new ApiParams()
                            .put("userPhone", phoneNum)
                            .put("userPass", password)
                            .put("regCode", regCode)
                            .put("promoterCode", promoterCode));

            // TODO: 7/22/16 统计数据 maybe delete
                    /*.put("deviceModel", "deviceModel")
                    .put("deviceImei", "deviceImei")
                    .put("deviceVersion", "deviceVersion")
                    .put("clientVersion", "clientVersion")
                    .put("regSource", "regSource")
                    .put("operator", "operator");*/
        }

        /**
         * 登录 /user/user/login.do
         *
         * @param phoneNum
         * @param password
         */
        public static API signIn(String phoneNum, String password) {
            try {
                if (!BuildConfig.DEBUG)  // TODO: 8/26/16 正式时候添加, 后期删除
                    password = SecurityUtil.md5Encrypt(password);
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
            // TODO: 2016/8/30 原来的网址
            return new API("/user/user/login.do",
                    new ApiParams()
                            .put("userPhone", phoneNum)
                            .put("userPass", password));

            // TODO: 7/22/16 统计数据 maybe delete
            /*      .put("deviceModel", deviceModel)
                    .put("deviceImei", deviceImei)
                    .put("deviceVersion", deviceVersion)
                    .put("clientVersion", clientVersion)
                    .put("operator", operator);*/
        }

        /**
         * /user/user/findUserInfo.do
         *
         * @return
         */
        public static API getUserInfo() {
            return new API("/user/user/findUserInfo.do", null);
        }

        /**
         * 接口名：找回密码并更新
         * <p>
         * URL  http://域名/user/user/retrieveUpdatePass.do
         *
         * @param userPhone 用户手机号
         * @param userPass  用户密码
         * @param regCode   短信验证码
         * @return
         */
//        public static API modifyPwdWhenFindPwd(String phone, String authCode, String newPwd) {
//            return new API("/user/user/findLoginPwd",
//                    new ApiParams()
//                            .put(TELE, phone)
//                            .put(AUTH_CODE, authCode)
//                            .put(PASSWORD, newPwd));
        public static API modifyPwdWhenFindPwd(String userPhone, String regCode, String userPass) {
            return new API("/user/user/retrieveUpdatePass.do",
                    new ApiParams()
                            .put("userPhone", userPhone)
                            .put("regCode", regCode)
                            .put("userPass", userPass));
        }


        /**
         * 接口名：获取找回密码图片验证码
         * <p>
         * URL  http://域名/user/user/getRetrieveImage.do
         *
         * @param userPhone
         * @return
         */
        public static API getRetrieveImage(String userPhone) {
            return new API("/user/user/getRetrieveImage.do",
                    new ApiParams()
                            .put(TELE, userPhone));
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
//        public static API getFundInfo(String token) {
//            return new API("/financy/financy/apiFinancyMain",
//                    new ApiParams()
//                            .put("token", token));
//        public static API getFundInfo(String token) {
//            return new API("/users/finance/findFlowList.do",
//                    new ApiParams().put("token", token));
//        }

        /**
         * 接口名：查询用户资金信息

         URL  http://域名/user/finance/findMain.do
         * @return
         */
        public static API getFundInfo() {
            return new API("/user/finance/findMain.do",
                    new ApiParams());
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

        /**
         * @param type     type=money为资金明细，type=score为积分明细
         * @param offset   流水起点
         * @param pageSize 流水显示条数
         * @return
         */
        public static API getFundSwitchIntegral(String type, int offset, int pageSize) {
            return new API("/users/finance/findFlowList.do",
                    new ApiParams()
                            .put(TYPE, type)
                            .put(PAGE_NO, offset)
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
            return new API(GET, "/order/variety/getVariety.do", null);
        }

        /**
         * /quota/quota/getAllQuotaData.do 获取产品行情数据
         *
         * @return
         */
        public static API getProductMarketList() {
            return new API(GET, "/quota/quota/getAllQuotaData.do", null);
        }
    }

    public static class Order {

        /**
         * /order/order/getUserPositionCount.do 获取产品订单持仓
         *
         * @return
         */
        public static API getHomePositions() {
            return new API("/order/order/getUserPositionCount.do", null);
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

        /**
         * /order/order/getTradeTime.do 获取当前市场状态;
         * <br/>可交易状态, 当前交易时间段截止时间
         * <br/>非交易状态, 下一个开市的时间
         *
         * @param exchangeId
         * @return
         */
        public static API getExchangeTradeStatus(int exchangeId) {
            return new API(GET, "/order/order/getTradeTime.do?exchangeId=" + exchangeId, null);
        }
    }

    /**
     * 获取品种分时图数据
     *
     * @param varietyType
     * @return
     */
    public static API getTrendData(String varietyType) {
        return new API(GET, "/quotaStatus/" + varietyType + ".fst", null);
    }
}
