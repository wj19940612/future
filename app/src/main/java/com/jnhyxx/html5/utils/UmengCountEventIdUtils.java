package com.jnhyxx.html5.utils;

import com.jnhyxx.html5.domain.market.Product;

/**
 * Created by ${wangJie} on 2016/12/20.
 * 友盟统计的自定义事件eventId
 */

public class UmengCountEventIdUtils {
    private static final String TAG = "UmengCountEventIdUtils";

    /**
     * me0100,头像,0
     * me0400,登录,0
     * me0500,注册,0
     * me0600,充值,0
     * me0601,银行卡支付,0
     * me0602,支付宝支付,0
     * me0603,充值页下一步,0
     * me0700,提现,0
     * me0701,提款记录,0
     * me0702,提现确定,0
     * me0800,设置,0
     * me0801,昵称,0
     * me0802,实名认证,0
     * me0803,绑定银行卡,0
     * me0804,退出登录,0
     * me0900,消息中心,0
     * me0901,系统消息详情,0
     * me1000,交易提醒详情,0
     * me1001,资金明细,0
     * me1002,金币明细,0
     * me1100,推广赚钱,0
     * me1101,我的用户,0
     * me1200,关于我们,0
     * me1201,公司简介,0
     * me1202,管理团队,0
     * me1203,企业文化,0
     * me1204,合作案例,0
     * me1205,联系电话,0
     * me1206,客服QQ,0
     * me1300,用户反馈,0
     * me1301,用户反馈页提交,0
     */

    //me0100,头像
    public static final String USER_HEAD = "me0100";
    // me0400,登录,0
    public static final String LOGIN = "me0400";
    // me0500,注册,0
    public static final String REGISTER = "me0500";
    //me0600,充值,0
    public static final String RECHARGE = "me0600";
    //me0601,银行卡支付,0
    public static final String PAY_BANK_CARD = "me0601";
    //me0602,支付宝支付,0
    public static final String PAY_ALIPAY = "me0602";
    //me0603,充值页下一步,0
    public static final String RECHARGE_SUBMIT = "me0603";
    //me0700,提现,0
    public static final String WITHDRAW = "me0700";
    //me0701,提款记录,0
    public static final String WITHDRAW_RECORD = "me0701";
    //me0702,提现确定,0
    public static final String WITHDRAW_OK = "me0702";
    //me0800,设置,0
    public static final String SET = "me0800";
    //me0801,昵称,0
    public static final String NICK_NAME = "me0801";
    //me0802,实名认证,0
    public static final String REAL_NAME = "me0802";
    //me0803,绑定银行卡,0
    public static final String BIND_BANK = "me0803";
    //me0804,退出登录,0
    public static final String LOGOUT = "me0804";
    //me0900,消息中心,0
    public static final String MESSAGE_CENTER = "me0900";
    // me0901,系统消息,0
    public static final String SYSTEM_MESSAGE = "me0901";
    //me0902,系统消息列表,详情0
    public static final String SYSTEM_MESSAGE_DETAILS = "me0902";
    // me1000,交易提醒,0 改一下
    public static final String TRADE_HINT = "me1000";
    // me1001,资金明细,0
    public static final String FUND_DETAIL = "me1001";
    //me1002,金币明细,0
    public static final String GOLD_DETAIL = "me1002";
    //me1100,推广赚钱,0
    public static final String EXPAND_EARN_MONEY = "me1100";
    //me1101,我的用户,0
    public static final String MINE_USER = "me1101";
    //me1200,关于我们,0
    public static final String ABOUT_US = "me1200";
    // me1201,公司简介,0
    public static final String COMPANY_PROFILE = "me1201";
    // me1202,管理团队,0
    public static final String MANAGE_TEAM = "me1202";
    //me1203,企业文化,0
    public static final String COMPANY_CULTURE = "me1203";
    //me1204,合作案例,0
    public static final String SHOW_CASE = "me1204";
    //me1205,联系电话,0
    public static final String CONNECT_PHONE = "me1205";
    //me1206,客服QQ,0
    public static final String SERVICE_QQ = "me1206";
    // me1300,用户反馈,0
    public static final String FEED_BACK = "me1300";
    // me1301,用户反馈页提交,0
    public static final String FEED_BACK_SUBMIT = "me1301";

    /**
     * home0100,banner,0
     * home0200,模拟操盘,0
     * home0201,模拟操盘页可用金币,0
     * home0202,模拟操盘页金币商城,0
     * home0300,首页新手引导,0
     * home0400首页联系客服,0
     */
    //home0100,banner,0
    public static final String BANNER = "home0100";
    //home0200,模拟操盘,0
    public static final String SIMULATION_TRADE = "home0200";
    //home0202,模拟操盘页金币商城,0
    public static final String SIMULATION_TRADE_GOLD_SHOP = "home0202";
    //home0300,首页新手引导,0
    public static final String HOME_PAGE_NEWBIE_GUIDE = "home0300";
    //home0400首页联系客服,0
    public static final String HOME_PAGE_CONNECT_SERVICE = "home0400";


    /**
     * gold0101,金币美原油,0
     * gold0102,金币美黄金,0
     * gold0103,金币恒指,0
     * gold0104,金币小纳指,0
     * gold0105,金币小恒指,0
     * gold0106,金币德指,0
     * gold0201,金币内盘沪镍,0
     * gold0202,金币内盘聚丙烯,0
     * gold0203,金币内盘沪银,0
     * gold0204,金币内盘螺纹钢,0
     * gold0205,金币内盘白糖,0
     * <p>
     * product0101,美原油,0
     * product0102,美黄金,0
     * product0103,恒指,0
     * product0104,小纳指,0
     * product0105,德指,0
     * product0201,内盘沪镍,0
     * product0202,内盘聚丙烯,0
     * product0203,内盘沪银,0
     * product0204,内盘螺纹钢,0
     * product0205,内盘白糖,0
     * <p>
     {备注说明：金币和实盘命名规则，gold指金币，product指实盘品种，
     数字前两位区分内外盘（00外盘01内盘），后两位区分品种代码)}
     */

    /**
     * gold指金币，product指实盘品种
     */
    public static final String FUND_TYPE_CASH = "product";
    public static final String FUND_TYPE_SIMULATION = "gold";

    public static final String DOMESTIC = "01";
    public static final String INTERNATIONAL = "00";

    public static String getProductUmengEventId(Product product, int fundType) {
        StringBuilder productEventId = new StringBuilder();
        productEventId.append(fundType == Product.FUND_TYPE_CASH ? FUND_TYPE_CASH : FUND_TYPE_SIMULATION);
        productEventId.append(product.isDomestic() ? DOMESTIC : INTERNATIONAL);
        productEventId.append(product.getVarietyType());
        return productEventId.toString();
    }

    /**
     * buy0200,玩法规则,0
     * buy0300,交易页右侧选择品种,0
     * buy0500,交易页持仓总盈亏,0
     * buy0600,交易页一键平仓,0
     * buy0700,订单,0
     * buy0701,订单持仓,0
     * buy07011,设置止盈止损,0
     * buy070111,设置止盈止损取消,0
     * buy070112,设置止盈止损确认设置,0
     * buy07012,订单持仓一键平仓,0
     * buy0702,订单结算,0
     * buy0703,订单结算列表,0
     * buy0800,分时图,0
     * buy0801,分时图盘面,0
     * buy0900,闪电图,0
     * buy1000,盘口,0
     * buy1100,日K,0
     * buy1200,买涨买跌,0
     * buy1201,确定买涨买跌,0
     * buy1300,闪电买涨买跌,0
     * buy1400,闪电下单开启入口,0
     * buy1401,开启闪电下单,0
     * buy1500,闪电下单关闭入口,0
     * buy1501,关闭闪电下单,0
     * buy1502,重新设置闪电下单,0
     * buy1503,闪电下单关闭入口（开启）,0
     */
    //buy0200,玩法规则,0
    public static final String GAME_RULES = "buy0200";
    //buy0300,交易页右侧选择品种,0
    public static final String MENU_SELECT_PRODUCT = "buy0300";
    //buy0500,交易页持仓总盈亏,0
    public static final String TRADE_POSITIONS_STATUS = "buy0500";
    //buy0600,交易页一键平仓,0
    public static final String TRADE_ONE_KEY_CLOSE_OUT = "buy0600";
    //buy0700,订单,0
    public static final String ORDER = "buy0700";
    //buy0701,订单持仓,0
    public static final String ORDER_POSITIONS = "buy0701";
    //buy07011,设置止盈止损,0
    public static final String SET_STOP_PROFIT_STOP_LOSS = "buy07011";
    // buy070111,设置止盈止损取消,0
    public static final String SET_STOP_PROFIT_STOP_LOSS_CANCEL = "buy070111";
    //buy070112,设置止盈止损确认设置,0
    public static final String SET_STOP_PROFIT_STOP_LOSS_OK = "buy070112";
    //buy07012,订单持仓一键平仓,0
    public static final String ORDER_POSITIONS_ONE_KEY_CLOSE_OUT = "buy07012";
    //buy0702,订单结算,0
    public static final String ORDER_CLEANING = "buy0702";
    // buy0703,订单结算列表,0
    public static final String ORDER_CLEANING_DETAILS = "buy0703";
    // buy0800,分时图,0
    public static final String TIME_SHARDED = "buy0800";
    // buy0801,分时图盘面,0
    public static final String TIME_SHARDED_PAGE = "buy0801";
    // buy0900,闪电图,0
    public static final String LIGHTNING = "buy0900";
    //buy1000,盘口,0
    public static final String HANDICAP = "buy1000";
    //buy1100,日K,0
    public static final String DAY_K = "buy1100";
    //buy1200,买涨买跌,0
    public static final String BUY_RISE_OR_BUY_DROP = "buy1200";
    //buy1201,确定买涨买跌,0
    public static final String BUY_RISE_OR_BUY_DROP_CONFIRM = "buy1201";
    //buy1300,闪电买涨买跌,0
    public static final String LIGHTNING_BUY_RISE_OR_BUY_DROP = "buy1300";
    //buy1400,闪电下单开启入口,0
    public static final String LIGHTNING_OPEN_DOOR = "buy1400";
    //buy1401,开启闪电下单,0
    public static final String OPEN_LIGHTNING_ORDERS = "buy1401";
    //buy1500,闪电下单关闭入口,0
    public static final String LIGHTNING_CLOSE_DOOR = "buy1500";
    //buy1501,关闭闪电下单,0
    public static final String ClOSE_LIGHTNING_ORDERS = "buy1501";
    //buy1502,重新设置闪电下单,0
    public static final String RESTART_SET_LIGHTNING_ORDERS = "buy1502";

    /**
     * live0100,节目单,0
     * live0200,直播页交易,0
     * live0300,老师头像,0
     * live0400,直播页播放,0
     * live0500,直播页全屏,0
     * live0600,静音,0
     * live0700,互动,0
     * live0800,老师指导,0
     * live0900,点击用户头像,0
     * live1000,发言,0
     * live1001,发送,0
     */
    //live0100,节目单,0
    public static final String PROGRAMME = "live0100";
    //live0200,直播页交易,0
    public static final String LIVE_TRADE = "live0200";
    //live0300,老师头像,0
    public static final String LIVE_TEACHER_IMAGE = "live0300";
    //live0400,直播页播放,0
    public static final String LIVE_PLAY = "live0400";
    //live0500,直播页全屏,0
    public static final String LIVE_FULL_SCREEN = "live0500";
    //live0600,静音,0
    public static final String LIVE_MUTE = "live0600";
    //live0700,互动,0
    public static final String LIVE_INTERACT = "live0700";
    //live0800,老师指导,0
    public static final String TEACHER_GUIDE = "live0800";
    //live0900,点击用户头像,0
    public static final String LIVE_USER_IMAGE = "live0900";
    //live1000,发言,0
    public static final String SPEAK = "live1000";
    //live1001,发送,0
    public static final String SEND_SPEAK = "live1001";
    //live1002,联系客服,0
    public static final String CONNECT_SERVICE = "live1002";

    /**
     * news0100,资讯直播,0              （杨 不做）
     * news0200,行情分析,0              （杨  不做）
     * news0201,行情分析详情,0
     * news0300,行情资讯,0                （杨 不做）
     * news0301,行情资讯详情,0
     * news0400,资讯tab,0
     */
    //news0100,资讯直播,0
    public static final String MESSAGE_LIVE = "news0100";
    //news0200,行情分析,0
    public static final String MARKET_ANALYZE = "news0200";
    //news0201,行情分析详情,0
    public static final String MARKET_ANALYZE_DETAILS = "news0201";
    //news0300,行情资讯,0
    public static final String MARKET_MESSAGE = "news0300";
    //news0301,行情资讯详情,0
    public static final String MARKET_MESSAGE_DETAILS = "news0301";

    /**
     * live1100,直播tab,0                   （新增）
     * news0400,资讯tab,0                   （新增）
     * me1400,我的tab,0                        (新增）
     * home0500,首页tab,0                    （新增）
     */
    //news0400,资讯tab,0
    public static final String TAB_MESSAGE = "news0400";
    //live1100,直播tab,0
    public static final String TAB_LIVE = "live1100";
    //me1400,我的tab,0
    public static final String TAB_MINE="me1400";
    //home0500,首页tab,0
    public static final String TAB_HOME="home0500";
}
