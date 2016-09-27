package com.jnhyxx.html5.utils;

import android.util.SparseArray;

/**
 * Created by ${wangJie} on 2016/9/27.
 * * 这是对交易明细中的资金明细或积分明细的remark字段进行处理的类
 * <p>
 * 提出的是交易明细界面第三栏的文字
 */

public class TradeDetailRemarkUtil extends SparseArray<String> {
    public TradeDetailRemarkUtil() {
        //银联充值
        super.put(1001, "成功");
        //支付宝充值
        super.put(1002, "成功");
        //微信充值
        super.put(1003, "成功");
        //宝付充值
        super.put(1004, "成功");
        //现在充值
        super.put(1005, "成功");
        //用户提现
        super.put(-1001, "申请");
        //拒绝提现
        super.put(-1002, "拒绝");
        //提现成功
        super.put(-1003, "成功");
//        //转账失败
//        super.put(-1004, "转账");
        //支付手续费
        super.put(-2001, "支付");
        //返还手续费
        super.put(2001, "返还");
        //冻结保证金
        super.put(-2002, "冻结");
        //返还保证金
        super.put(2002, "返还");

        //收益填合约
        //收益增加
        super.put(2003, "");
        //收益减少
        super.put(-2003, "");
//        //充值红包
//        super.put(3001, "红包");
//        //抹除红包
//        super.put(-3001, "红包");
        //充值积分
        super.put(3002, "购买");
        //抹除积分
        super.put(-3002, "兑换");
        //充值资金
        super.put(3003, "资金");
        //抹除资金
        super.put(-3003, "资金");
        //系统补单
        super.put(3004, "成功");
        //内部充值资金
        super.put(3005, "成功");
        //内部充值积分
        super.put(3006, "充值");
//        //支付宝转账
//        super.put(-4001, "充值");
//        //佣金存入
//        super.put(5001, "佣金");
//        //佣金转出
//        super.put(-5001, "佣金");
        //佣金转余额
        super.put(5002, "存入");
    }
}
