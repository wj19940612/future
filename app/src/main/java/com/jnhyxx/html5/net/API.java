package com.jnhyxx.html5.net;

import android.util.Log;

import com.android.volley.Request;
import com.jnhyxx.html5.Preference;
import com.jnhyxx.html5.domain.account.UserDefiniteInfo;
import com.jnhyxx.html5.domain.finance.SupportApplyWay;
import com.jnhyxx.html5.domain.local.SubmittedOrder;
import com.jnhyxx.html5.domain.order.LightningOrderAsset;
import com.johnz.kutils.SecurityUtil;
import com.johnz.kutils.net.ApiParams;

import java.io.File;
import java.security.NoSuchAlgorithmException;


public class API extends APIBase {
    private static final String TAG = "API";
    private static final int GET = Request.Method.GET;

    public static final String TOKEN = "token";
    public static final String PAGE_NO = "pageNo";
    public static final String PAGE_SIZE = "pageSize";
    public static final String TYPE = "type";
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
         * 获取注册短信验证码 /user/user/getRegCode.do
         *
         * @param tele
         */
        public static API obtainAuthCode(String tele, String regImageCode) {
            return new API("/user/user/getRegCode.do",
                    new ApiParams()
                            .put("userPhone", tele)
                            .put("regImageCode", regImageCode));
        }

        /**
         * 找回密码时候获取短信验证码 /user/user/retrievePass.do
         *
         * @param userPhone
         */
        public static API obtainAuthCodeWhenFindPwd(String userPhone, String regImageCode) {
            return new API("/user/user/retrievePass.do",
                    new ApiParams()
                            .put("userPhone", userPhone)
                            .put("regImageCode", regImageCode));

        }

        /**
         * 找回登录密码 - 验证码验证 /user/user/checkRetriveMsgCode.do
         *
         * @param userPhone
         * @param code
         * @return
         */
        public static API authCodeWhenFindPassword(String userPhone, String code) {
            return new API("/user/user/checkRetriveMsgCode.do",
                    new ApiParams()
                            .put("userPhone", userPhone)
                            .put("msgCode", code));
        }

        /**
         * 注册 /user/register.do
         *
         * @param phoneNum
         * @param password
         * @param regCode
         */
        public static API register(String phoneNum, String password, String regCode,
                                   String promoterCode, String channel) {
            try {
                password = SecurityUtil.md5Encrypt(password);
                Log.d(TAG, "注册时密码MD5加密" + password);
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
            return new API("/user/user/register.do",
                    new ApiParams()
                            .put("userPhone", phoneNum)
                            .put("userPass", password)
                            .put("regCode", regCode)
                            .put("promoterCode", promoterCode)
                            .put("deviceId", Preference.get().getPushClientId())
                            .put("platform", 0)
                            .put("source", channel)
                            .put("sourceBackup", channel));
        }

        /**
         * 登录 /user/user/login.do
         *
         * @param phoneNum
         * @param password
         */
        public static API login(String phoneNum, String password) {
            try {
                password = SecurityUtil.md5Encrypt(password);
                Log.d(TAG, "登陆密码MD5加密" + password);
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }

            return new API("/user/user/login.do",
                    new ApiParams()
                            .put("userPhone", phoneNum)
                            .put("userPass", password)
                            .put("deviceId", Preference.get().getPushClientId())
                            .put("platform", 0));
        }

        /**
         * 找回密码: 修改密码 /user/user/updatePass.do
         *
         * @param userPhone
         * @param userPass
         * @return
         */
        public static API modifyPwdWhenFindPwd(String userPhone, String userPass) {
            try {
                userPass = SecurityUtil.md5Encrypt(userPass);
                Log.d(TAG, "找回密码密码MD5加密" + userPass);
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
            return new API("/user/user/updatePass.do",
                    new ApiParams()
                            .put("userPhone", userPhone)
                            .put("userPass", userPass));
        }

        /**
         * /user/user/authUser 实名认证
         *
         * @param realName
         * @param identityNum
         * @return
         */
        public static API authUserName(String realName, String identityNum) {
            return new API("/user/user/certification.do",
                    new ApiParams()
                            .put("realName", realName)
                            .put("idCard", identityNum));
        }

        /**
         * 接口名：绑定银行卡
         * <p/>
         * URL  http://域名/user/user/bindBankCard.do
         * bankId        Integer   银行列表
         * bankName      String     银行名
         * cardNumber    String    银行卡号
         * cardPhone     String    银行卡对应的手机号
         *
         * @param bankId
         * @param bankName
         * @return
         */
        public static API bindBankCard(Integer bankId, String bankName, String cardNumber, String cardPhone) {
            return new API("/user/user/bindBankCard.do",
                    new ApiParams()
                            .put("bankId", bankId)
                            .put("bankName", bankName)
                            .put("cardNumber", cardNumber)
                            .put("cardPhone", cardPhone));
        }

        /**
         * 接口名：显示渠道银行列表
         * <p/>
         * URL  http://域名/user/user/showChannelBankList.do
         *
         * @return
         */
        public static API showChannelBankList() {
            return new API("/user/user/showChannelBankList.do", new ApiParams());
        }


        /**
         * 退出
         *
         * @return
         */
        public static API logout() {
            return new API("/user/user/logout.do", new ApiParams());
        }

        /**
         * 接口名：修改昵称
         * <p/>
         * http://域名/user/user/updateNickName.do
         *
         * @param nickName
         * @return
         */
        public static API updateNickName(String nickName) {
            return new API("/user/user/updateNickName.do",
                    new ApiParams()
                            .put("nickName", nickName));
        }

        /**
         * 接口名：查询资讯列表
         * URL  http://域名/user/news/findNewsList.do
         *
         * @param type   资讯类型   0为首页资讯,1为列表资讯,2为弹窗资讯
         * @param offset 资讯起始点
         * @param size   资讯显示数量
         * @return
         */
        public static API getNewsList(int type, int offset, int size) {
            return new API(GET, "/user/news/findNewsList.do", new ApiParams()
                    .put("type", type)
                    .put("offset", offset)
                    .put("size", size));
        }


        /**
         * /user/user/findPromoterCode.do
         *
         * @return
         */
        public static API getPromoteCode() {
            return new API("/user/user/findPromoterCode.do", null);
        }

        /**
         * /user/user/toBePromoter.do 成为推广员
         *
         * @return
         */
        public static API becomePromoter() {
            return new API("/user/user/toBePromoter.do", null);
        }

        /**
         * 接口名：获取找回密码图片验证码 /user/user/getRetrieveImage.do
         *
         * @param userPhone
         * @return
         */
        public static String getFindPwdAuthCodeImage(String userPhone) {
            return getHost() + "/user/user/getRetrieveImage.do" + "?userPhone=" + userPhone;
        }

        /**
         * 接口名：获取注册图片验证码  user/user/getRegImage.do
         *
         * @param userPhone
         * @return
         */
        public static String getRegisterAuthCodeImage(String userPhone) {
            return getHost() + "/user/user/getRegImage.do" + "?userPhone=" + userPhone;
        }

        /**
         * http://newtest.jnhyxx.com/user/user/getChannelByDomain.do
         *
         * @return 获取渠道客服的qq和电话
         */
        public static API getChannelByDomain() {
            return new API("/user/user/getChannelByDomain.do", new ApiParams());
        }

        /**
         * /user/user/findUserInfo.do 获取用户非完整数据
         *
         * @return
         */
        public static API getUserShortInfo() {
            return new API("/user/user/findUserInfo.do", null);
        }

        public static API submitFeedBack(String content, String userId, String userName, String realName, String userPhone) {
            return new API("/user/userFeedback/insert.do",
                    new ApiParams()
                            .put("content", content)
                            .put("userId", userId)
                            .put("userName", userName)
                            .put("realName", realName)
                            .put("userPhone", userPhone));
        }

        /**
         * 接口名：展示用户信息
         * URL  http://域名/user/user/findInfo.do
         *
         * @param userId 用户id
         * @return
         */
        public static API getUserInfo() {
            return new API("/user/user/findInfo.do", null);
        }

        /**
         * 接口名：修改用户信息
         * URL  http://域名/user/user/updateInfo.do
         *
         * @param userDefiniteInfo
         * @return
         */
        public static API submitUserInfo(UserDefiniteInfo userDefiniteInfo) {
            return new API("/user/user/updateInfo.do", new ApiParams(UserDefiniteInfo.class, userDefiniteInfo));
        }


        /**
         * 接口名：修改用户头像信息
         * URL  http://域名/user/user/updatePic.do
         *
         * @param
         * @return
         */

        public static API updateUserHeadImage(String bitmapto64) {
            return new API("/user/user/updatePic.do",
                    new ApiParams().put("pic", bitmapto64));

        }
    }

    public static class Finance {

        /**
         * 接口名：查询用户资金信息
         * <p/>
         * URL  http://域名/user/finance/findMain.do
         *
         * @return
         */
        public static API getFundInfo() {
            return new API("/user/finance/findMain.do",
                    new ApiParams());
        }

        /**
         * 接口名：用户充值(银行卡充值)
         * <p>
         * URL  http://域名/user/finance/deposit.do
         *
         * @param money
         * @return
         */
        public static String depositByBankApply(double money) {
            return getHost() + "/user/finance/deposit.do?&money=" + money;
        }

        /**
         * 微信充值
         *
         * @param money
         * @return
         */
        public static String depositByWeChartApply(double money) {
            return getHost() + "/user/finance/depositByWeChat.do?" + "&money=" + money;
        }

        /**
         * 接口名：用户充值(支付宝充值)
         * URL  http://域名/user/finance/depositByAlipay.do
         *
         * @param money
         * @return
         */
        public static String depositByAliPay(double money) {
            return getHost() + "/user/finance/depositByAlipay.do?" + "platform=" + SupportApplyWay.ALI_PAY_DEPOSIT_ANDROID + "&money=" + money;
        }

        /**
         * 接口名：查询当前渠道所支持的所有支付渠道
         * <p>
         * http://域名/user/finance/findDepositType.do
         */
        public static API getSupportApplyWay() {
            return new API("/user/finance/findDepositType.do", new ApiParams());
        }


        /**
         * 判断用户是否签署过支付协议
         * //http://newtest.jnhyxx.com/user/finance/checkTopupRead.do
         *
         * @return
         */
        public static API isUserAgreePayment() {
            return new API("/user/finance/checkTopupRead.do", null);
        }

        /**
         * 打开用户同意支付协议的h5页面
         * /xieyi/riskWarning.html
         */
        public static String getUserAgreePaymentPagePath() {
            return getHost() + ("/xieyi/riskWarning.html?nohead=1");
        }

        //用户同意支付协议后h5返回的网址头
        public static String getUserAggressPaymentConfirmPagePath() {
            return getHost() + "/account/banks.html";
        }

        /**
         * 接口名：支付成功页面通知地址
         * <p>
         * URL  http://域名/user/finance/page.do
         *
         * @return
         */
        public static String getRechargeSuccessUrl() {
            return getHost() + ("/user/finance/page.do");
        }

        /**
         * 接口名：支付出现异常时返回的商户页面
         * URL  http://域名/user/finance/back.do
         *
         * @return
         */
        public static String getRechargeFailUrl() {
            return getHost() + ("/user/finance/back.do");
        }

        /**
         * 宝付充值成功返回的地址
         */
        public static String getMineWebPageUrl() {
            return getHost() + ("/mine.html");
        }

        /**
         * 接口名：用户提现
         * <p>
         * URL  http://域名/user/finance/draw.do
         *
         * @param money
         */
        public static API withdraw(double money) {
            return new API("/user/finance/draw.do",
                    new ApiParams()
                            .put("money", money));
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
         * 接口名：资金或积分明细
         * <p>
         * URL  http://域名/user/finance/findFlowList.do
         *
         * @param type     type=money为资金明细，type=score为积分明细
         * @param offset   流水起点
         * @param pageSize 流水显示条数
         * @return
         */
        public static API getFundSwitchIntegral(String type, int offset, int pageSize) {
            return new API(GET, "/user/finance/findFlowList.do",
                    new ApiParams()
                            .put("type", type)
                            .put("offset", offset)
                            .put("size", pageSize));
        }

        /**
         * 接口名：查询用户单笔提现记录详细信息
         * URL  http://域名/user/finance/findIOInfo.do
         *
         * @param type type=-1提现记录 提现记录id号
         * @param id
         * @return
         */
        public static API getWithdrawRecordInfo(int type, int id) {
            return new API("/user/finance/findIOInfo.do", new ApiParams()
                    .put("type", type)
                    .put("id", id));
        }

        /**
         * 接口名：查询用户提现记录
         * URL  http://域名/user/finance/findIOList.do
         *
         * @param type   type=-1提现记录
         * @param offset 提现记录起始点
         * @param size   每次提现记录显示条数
         * @return
         */
        public static API getWithdrawRecordList(int type, int offset, int size) {
            return new API("/user/finance/findIOList.do", new ApiParams()
                    .put("type", type)
                    .put("offset", offset)
                    .put("size", size));
        }
    }

    //直播的接口
    public static class Live {

        /**
         * 接口名：一大块，获取直播的节目单，直播的老师信息及头像，直播公告
         * URL  http://域名/user/live/getLiveMessage.do
         *
         * @return
         */
        public static API getLiveMessage() {
            return new API(GET, "/user/live/getLiveMessage.do", null);
        }

        /**
         * 接口名：分页获取直播老师指导
         * URL  http://域名/user/live/findTeacherMsgList.do
         *
         * @param offset    偏移量
         * @param pageSize
         * @param teacherId 老师有两个id，一个是老师介绍等信息的主键，一个是创建老师登录账户的id
         * @return
         */
        public static API getTeacherGuide(int offset, int pageSize, int teacherId) {
            return new API(GET, "/user/live/findTeacherMsgList.do", new ApiParams()
                    .put("offset", offset)
                    .put("size", pageSize)
                    .put("teacherId", teacherId));
        }

        /**
         * 获取最后一条老师指导
         *
         * @param teacherAccountId
         * @return
         */
        public static API getLastTeacherGuide(int teacherAccountId) {
            return new API(GET, "/user/live/findFirstTeacherMsg.do",
                    new ApiParams()
                            .put("teacherId", teacherAccountId));
        }


        /**
         * 接口名：按条件查询
         * URL  http://域名/user/live/findChatList.do
         *
         * @param offset   偏移量
         * @param pageSize 每页请求数量
         * @return
         */
        public static API getLiveTalk(int offset, int pageSize) {
            return new API(GET, "/user/live/findChatList.do", new ApiParams()
                    .put("offset", offset)
                    .put("size", pageSize));
        }
    }

    public static class Message {

        /**
         * 接口名：查询行情分析和行情咨询
         * URL  http://域名/user/news/loadNews.do
         *
         * @param pushType 0代表是 行情资讯 1 代表行业资讯  2 代表系统消息 3 代表 交易提醒
         * @param page     页数 从0开始
         * @param pageSize 当前展示多少数量
         * @return
         */
        public static API getMessageInfo(int pushType, int page, int pageSize) {
            return new API("/user/news/loadNews.do", new ApiParams()
                    .put("pushType", pushType)
                    .put("page", page)
                    .put("pageSize", pageSize));
        }

        /**
         * 接口名：查询资讯列表
         * URL  http://域名/user/news/findNewsList.do
         *
         * @param type   资讯类型  首页banner0,行情分析2，行业分析3
         * @param offset 资讯起始点
         * @param size   资讯显示数量
         * @return
         */
        public static API findNewsList(int type, int offset, int size) {
            return new API(GET, "/user/news/findNewsList.do", new ApiParams()
                    .put("type", type)
                    .put("offset", offset)
                    .put("size", size));
        }

        /**
         * 查询咨询详情
         * URL  http://域名/user/news/findNews.do
         *
         * @param id
         * @return
         */
        public static API findNewsInfo(String id) {
            return new API("/user/news/findNews.do", new ApiParams()
                    .put("id", id));
        }

        /**
         * 接口名：查询资讯(通过第三方地址)
         * URL  http://域名/user/news/findNewsByUrl.do
         *
         * @param url
         * @return
         */
        public static API findNewsByUrl(String url) {
            return new API(GET, "/user/news/findNewsByUrl.do",
                    new ApiParams()
                            .put("url", url));
        }

        /**
         * 获取大厅 弹出框
         *
         * @return
         */
        public static API getHomePopup() {
            return new API(GET, "/user/news/getPopPush.do", null);
        }

        //资讯直播的大图网址
        public static String getMessageLiveInfoUrl() {
            return "https://res.6006.com/jin10/";
        }

        //资讯直播中星星的网址
        public static String getMessageLiveStarUrl(String star) {
            return "https://res.6006.com/assets/ext/news/" + star + ".png";
        }

        //资讯直播组织的图标  https://res.6006.com/jin10/flag/法国.png
        public static String getOrganizeMarkUrl(String markUrl) {
            return getMessageLiveInfoUrl() + "/flag/" + markUrl + ".png";
        }
    }

    public static class Market {

        /**
         * /order/variety/getVariety.do 获取首页产品列表
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

        /**
         * /quota/quota/getAllIpPortByCode.do 获取行情服务器 ip & port
         *
         * @return
         */
        public static API getMarketServerIpAndPort() {
            return new API(GET, "/quota/quota/getAllIpPortByCode.do", null);
        }

        /**
         * 获取闪电下单状态
         *
         * @param varietyId 品种id
         * @param payType   支付方式  0：积分 1：现金
         * @return
         */
        public static API getOrderAssetStoreStatus(int varietyId, int payType) {
            return new API(GET, "/order/orderAssetsStore/getAssetsStore.do",
                    new ApiParams()
                            .put("varietyId", varietyId)
                            .put("payType", payType));
        }

        /**
         * 闪电下单更新配置
         * URL  http://域名/order/orderAssetsStore/saveAndUpdate.do
         *
         * @return
         */

        public static API saveAndUpdateOrderAssetStore(LightningOrderAsset lightningOrderAsset) {
            return new API("/order/orderAssetsStore/saveAndUpdate.do",
                    new ApiParams(LightningOrderAsset.class, lightningOrderAsset));
        }

        /**
         * 删除闪电下单配置
         *
         * @param varietyId 品种id
         * @param payType   支付方式  0：积分 1：现金
         * @return
         */
        public static API removeOrderAssetStoreStatus(int varietyId, int payType) {
            return new API("/order/orderAssetsStore/removeAssetsStore.do", new ApiParams()
                    .put("varietyId", varietyId)
                    .put("payType", payType));
        }

        /**
         * /quota/quota/getAllIpPortByCode.do 获取聊天服务器 ip & port
         *
         * @return
         */
        public static API getChattingServerIpAndPort() {
            return new API(GET, "/quota/quota/getAllIpPortByCode.do",
                    new ApiParams()
                            .put("type", "chatApp"));
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
         * /order/order/getVarietySettleOrders.do 获取结算订单列表
         *
         * @param pageNo
         * @param pageSize
         * @param varietyId
         * @param payType
         * @return
         */
        public static API getSettlementOrderList(int varietyId, int payType, int pageNo, int pageSize) {
            return new API(GET, "/order/order/getVarietySettleOrders.do",
                    new ApiParams()
                            .put("varietyId", varietyId)
                            .put("payType", payType)
                            .put(PAGE_NO, pageNo)
                            .put(PAGE_SIZE, pageSize));
        }

        /**
         * /order/order/getTradeTime.do 获取当前市场状态;
         * <br/>可交易状态, 当前交易时间段截止时间
         * <br/>非交易状态, 下一个开市的时间
         *
         * @param exchangeId
         * @return
         */
        public static API getExchangeTradeStatus(int exchangeId, String varietyType) {
            return new API(GET, "/order/order/getTradeTime.do",
                    new ApiParams()
                            .put("exchangeId", exchangeId)
                            .put("varietyType", varietyType));
        }

        /**
         * /order/variety/getAssetsByVariety.do 获取期货配资数据
         *
         * @param varietyId
         */
        public static API getFuturesFinancing(int varietyId, int payType) {
            return new API(GET, "/order/variety/getAssetsByVariety.do",
                    new ApiParams()
                            .put("varietyId", varietyId)
                            .put("payType", payType));
        }

        /**
         * /order/order/submitOrder.do 提交订单
         *
         * @param submittedOrder
         */
        public static API submitOrder(SubmittedOrder submittedOrder) {
            return new API("/order/order/submitOrder.do",
                    new ApiParams(SubmittedOrder.class, submittedOrder));
        }

        /**
         * /order/order/getOrderInfo.do 获取结算订单详情
         *
         * @param showId
         * @param fundType
         * @return
         */
        public static API getOrderDetail(String showId, int fundType) {
            return new API(GET, "/order/order/getOrderInfo.do",
                    new ApiParams()
                            .put("showId", showId)
                            .put("payType", fundType));
        }

        /**
         * /order/order/getVarietyPositionOrders.do 获取用户持仓中订单
         *
         * @param varietyId
         * @param fundType
         * @return
         */
        public static API getHoldingOrderList(int varietyId, int fundType) {
            return new API(GET, "/order/order/getVarietyPositionOrders.do",
                    new ApiParams()
                            .put("varietyId", varietyId)
                            .put("payType", fundType));
        }

        /**
         * /order/order/getOrderStatus.do 获取订单状态
         *
         * @param showId
         * @return
         */
        public static API getOrderStatus(String showId) {
            return new API(GET, "/order/order/getOrderStatus.do",
                    new ApiParams()
                            .put("showId", showId));
        }

        /**
         * /order/order/unwind.do 平仓
         *
         * @param showId
         * @param payType
         * @param unwindPrice
         * @return
         */
        public static API closeHoldingOrder(String showId, int payType, double unwindPrice) {
            return new API("/order/order/unwind.do",
                    new ApiParams()
                            .put("showId", showId)
                            .put("payType", payType)
                            .put("unwindPrice", unwindPrice));
        }

        /**
         * /order/order/aKeyUnwind.do 一键平仓
         *
         * @param showIds
         * @param payType
         * @param unwindPrices
         * @return
         */
        public static API closeAllHoldingOrders(String showIds, int payType, String unwindPrices) {
            return new API("/order/order/aKeyUnwind.do",
                    new ApiParams()
                            .put("showIds", showIds)
                            .put("payType", payType)
                            .put("unwindPrice", unwindPrices));
        }

        /**
         * order/variety/getVarietyFloating.do 获取二次设置止盈止损的启动与否
         *
         * @param varietyId
         * @return
         */
        public static API getStopProfitLossActive(int varietyId, int payType) {
            return new API(GET, "/order/variety/getVarietyFloating.do",
                    new ApiParams()
                            .put("varietyId", varietyId)
                            .put("payType", payType));
        }

        /**
         * /order/order/getOrderVarietyPrice.do 获取二次设置止盈止损的配置
         *
         * @param showId
         * @param payType
         * @return
         */
        public static API getStopProfitLossConfig(String showId, int payType) {
            return new API(GET, "/order/order/getOrderVarietyPrice.do", new ApiParams()
                    .put("showId", showId)
                    .put("payType", payType));
        }

        /**
         * /order/order/updateOrder.do 提交更新止盈止损
         *
         * @param showId
         * @param payType
         * @param loseMoney
         * @param winMoney
         * @return
         */
        public static API updateStopProfitLoss(String showId, int payType, double loseMoney, double winMoney) {
            return new API("/order/order/updateOrder.do", new ApiParams()
                    .put("showId", showId)
                    .put("payType", payType)
                    .put("loseMoney", loseMoney)
                    .put("winMoney", winMoney));
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

    /**
     * 获取 k 线数据
     *
     * @param varietyType
     * @param type
     * @return
     */
    public static API getKlineData(String varietyType, String type) {
        return new API(GET, "/quotaData/candlestickData/getCandlesticKData.do",
                new ApiParams()
                        .put("contractsCode", varietyType));
    }

    /**
     * 获得《投资人与用户交易合作协议》网页 url
     *
     * @return
     */
    public static String getCooperationAgreementUrl() {
        return getHost() + "/activity/Article.html?type=3&nohead=1";
    }

    /**
     * 获得《风险告知书》网页 url
     *
     * @return
     */
    public static String getRiskNoticesUrl() {
        return getHost() + "/activity/Article.html?type=4&nohead=1";
    }

    /**
     * 获取 交易规则 url
     *
     * @param varietyId
     * @return
     */
    public static String getTradeRule(int varietyId) {
        return getHost() + "/activity/Article.html?type=2&varietyId=" + varietyId + "&nohead=1";
    }

    /**
     * 推广赚钱 url
     */
    public static String getPromotePage() {
        return getHost() + "/mine/extension.html?nohead=1";
    }

    /**
     * 推广赚钱 我的用户 url
     */
    public static String getPromoteMyUsers() {
        return getHost() + "/mine/users.html?nohead=1";
    }

    /**
     * 注册界面的服务协议网址
     */
    public static String getRegisterServiceProtocol() {
        return getHost() + "/activity/Article.html?type=5&nohead=1";
    }

    public static String getServiceQQ(String serviceQQ) {
        return "mqqwpa://im/chat?chat_type=wpa&uin=" + serviceQQ + "&version=1";
    }

    /**
     * findNewsByUrl  中所需要的参数
     *
     * @return
     */
    public static String getInfoLiveUrl() {
        return "http://m.jin10.com/flash?maxId=0";
    }

    /**
     * 新手引导页面网址
     *
     * @return
     */
    public static String getNewbieUrl() {
        return getHost() + "/newtrader.html?nohead=1";
    }
}
