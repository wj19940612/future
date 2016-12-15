package com.jnhyxx.html5.domain.finance;

import java.io.Serializable;

/**
 * Created by ${wangJie} on 2016/9/28.
 * 目前所支持的支付方式
 */

public class SupportApplyWay implements Serializable {

    public static final int ALI_PAY_DEPOSIT_ANDROID = 1;

    //银行卡支付
    public static final int DEPOSIT_BY_BANK_APPLY_PAY = 0;
    //支付宝支付
    public static final int DEPOSIT_BY_ALI_PAY_PAY = 1;
    //微信支付
    public static final int DEPOSIT_BY_BANK_WE_CHART_PAY = 2;


    /**
     * bank银行卡支付
     */
    private boolean bank;
    /**
     * alipay支付宝支付
     */
    private boolean alipay;
    /**
     * wechat微信支付
     */
    private boolean wechat;

    public boolean isBank() {
        return bank;
    }

    public void setBank(boolean bank) {
        this.bank = bank;
    }

    public boolean isAlipay() {
        return alipay;
    }

    public void setAlipay(boolean alipay) {
        this.alipay = alipay;
    }

    public boolean isWechat() {
        return wechat;
    }

    public void setWechat(boolean wechat) {
        this.wechat = wechat;
    }

    @Override
    public String toString() {
        return "SupportApplyWay{" +
                "bank=" + bank +
                ", alipay=" + alipay +
                ", wechat=" + wechat +
                '}';
    }
}
