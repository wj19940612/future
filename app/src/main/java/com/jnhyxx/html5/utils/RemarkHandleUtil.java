package com.jnhyxx.html5.utils;

import android.util.SparseArray;

import com.jnhyxx.html5.domain.account.TradeDetail;

/**
 * Created by ${wangJie} on 2016/9/21.
 * 这是对交易明细中的资金明细或积分明细的remark字段进行处理的类
 * <p>
 * 提出的是交易明细界面第二栏的文字
 */

public class RemarkHandleUtil extends SparseArray<String> {

    public RemarkHandleUtil() {
        //支付宝充值
//        super.put(1002, "充值");
//        //微信充值
//        super.put(1003, "充值");
//        //宝付充值
//        super.put(1004, "充值");
//        //现在充值
//        super.put(1005, "充值");
        //用户提现
        super.put(-1201, "提现");
        //拒绝提现
        super.put(1201, "提现");
        //支付手续费
        super.put(-2101, "手续费");
        //返还手续费
        super.put(2301, "手续费");
        //冻结保证金
        super.put(-2102, "保证金");
        // TODO: 2016/10/13 两个返回保证金 
        //返还保证金
        super.put(2201, "保证金");
        //返还保证金
        super.put(2302, "保证金");
        //收益增加
        super.put(2202, "收益");
        //收益减少
        super.put(-2202, "收益");
        //内部充值资金
        super.put(3101, "充值");
        //内部提现资金
        super.put(-3101, "提现");
        //内部提现资金拒绝
        super.put(3102, "提现");
        //内部充值积分
        super.put(3201, "金币");
        //内部取出积分
        super.put(-3202, "金币");
        //内部取出积分
        super.put(-3201, "金币");
        //系统补单
        super.put(4101, "充值");
        //注册赠送
        super.put(4201, "赠送");
        //佣金转余额
        super.put(6001, "佣金");
        //兑换积分-7101
        super.put(-7101, "金币");
        //资金兑换
        super.put(7201, "金币");
        //-7201兑换商品
        super.put(-7201, "商品");
        //拒绝兑换
        super.put(7202, "金币");

    }

    @Override
    public String get(int key, String valueIfKeyNotFound) {
        return super.get(key, "");
    }

    public static boolean isRecharge(int type) {
        return type == TradeDetail.RECHARGE_TYPE;
    }
}
