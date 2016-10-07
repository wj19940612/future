package com.jnhyxx.html5.utils;

import android.util.SparseArray;

/**
 * Created by ${wangJie} on 2016/9/21.
 * 这是对交易明细中的资金明细或积分明细的remark字段进行处理的类
 *
 * 提出的是交易明细界面第二栏的文字
 */

public class RemarkHandleUtil extends SparseArray<String> {

    public RemarkHandleUtil() {
        super.put(1001, "充值");
        //支付宝充值
        super.put(1002, "充值");
        //微信充值
        super.put(1003, "充值");
        //宝付充值
        super.put(1004, "充值");
        //现在充值
        super.put(1005, "充值");
        //用户提现
        super.put(-1001, "提现");
        //拒绝提现
        super.put(-1002, "提现");
        //提现成功
        super.put(-1003, "提现");
//        //转账失败
//        super.put(-1004, "转账");
        //支付手续费
        super.put(-2001, "手续费");
        //返还手续费
        super.put(2001, "手续费");
        //冻结保证金
        super.put(-2002, "保证金");
        //返还保证金
        super.put(2002, "保证金");
        //收益增加
        super.put(2003, "收益");
        //收益减少
        super.put(-2003, "收益");
//        //充值红包
//        super.put(3001, "红包");
//        //抹除红包
//        super.put(-3001, "红包");
        //充值积分
        super.put(3002, "金币");
        //抹除积分
        super.put(-3002, "金币");
        //充值资金
        super.put(3003, "资金");
//        //抹除资金
//        super.put(-3003, "资金");
        //系统补单
        super.put(3004, "充值");
        //内部充值资金
        super.put(3005, "充值");
        //内部体现资金
        super.put(-3005, "体现");
        //内部充值积分
        super.put(3006, "金币");
        //支付宝转账
        super.put(-4001, "充值");
        //佣金存入
        super.put(5001, "佣金");
//        //佣金转出
//        super.put(-5001, "佣金");
        //佣金转余额
        super.put(5002, "佣金");
    }

    @Override
    public String get(int key, String valueIfKeyNotFound) {
        return super.get(key, "");
    }
}
