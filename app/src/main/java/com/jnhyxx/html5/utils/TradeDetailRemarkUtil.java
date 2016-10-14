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
        //支付宝充值
        super.put(1002, "成功");
        //微信充值
        super.put(1003, "成功");
        //宝付充值
        super.put(1004, "成功");
        //现在充值
        super.put(1005, "成功");
        //用户提现
        super.put(-1201, "申请");
        //拒绝提现
        super.put(1201, "拒绝");
        //支付手续费
        super.put(-2101, "支付");
        //返还手续费
        super.put(2301, "返还");
        //冻结保证金
        super.put(-2102, "冻结");
        //返还保证金
        super.put(2201, "返还");

        //收益填合约
        //收益增加
        super.put(2202, "");
        //收益减少
        super.put(-2202, "");
        //系统补单
        super.put(3004, "成功");
        //返还手续费
        super.put(2301, "返还");
        //返还保证金
        super.put(2302, "返还");
        //内部充值资金
        super.put(3101, "成功");
        //内部取出资金
        super.put(-3101, "提现");
        //内部充值积分
        super.put(3201, "成功");
        //内部取出积分
        super.put(-3202, "提现");
        //系统补单
        super.put(4101, "成功");
        //注册赠送
        super.put(4201, "成功");
        //佣金转余额
        super.put(6001, "存入");
    }
}
