package com.jnhyxx.html5.domain.finance;

import java.io.Serializable;

/**
 * Created by ${wangJie} on 2016/9/22.
 * 提现记录详情界面
 */

public class WithDrawRecordInfo implements Serializable {
    private static final long serialVersionUID = 2106333055690785589L;


    /**
     * 表示刚刚发起
     */
    public static final int START_TRADE = 0;
    /**
     * 1表示审批通过
     */
    public static final int AUDIT_PASSING = 1;
    /**
     * 2表示转账中
     */
    public static final int FUND_TRANSFER = 2;
    /**
     * 3表示充提成功
     */
    public static final int RECHARGE_OR_WITHDRAW_SUCCESS = 3;
    /**
     * 4表示提现拒绝
     */
    public static final int WITHDRAW_refuse = 4;
    /**
     * 5表示转账失败
     */
    public static final int TRANSFER_FAIL = 5;

    /**
     * cardNumber : 6217001540440079983
     * commission : 0
     * createTime : 2016-08-19 	14:14:05
     * id : 162
     * issuingBankName : 中国建设银行
     * money : 0.01
     * selfOrderId : tx371471587248967
     * status : 0
     * updateTime : 2016-08-19 14:14:05
     */

    /**
     * 银行卡号
     */
    private String cardNumber;
    /**
     * 提现手续费
     */
    private double commission;
    /**
     * 提现申请时间
     */
    private String createTime;
    private int id;
    /**
     * 银行名
     */
    private String issuingBankName;
    /**
     * 提现金额
     */
    private double money;
    /**
     * 内部订单号
     */
    private String selfOrderId;
    /**
     * 0表示刚刚发起
     * 1表示审批通过
     * 2表示转账中
     * 3表示充提成功
     * 4表示提现拒绝
     * 5表示转账失败
     */
    private int status;
    /**
     * 提现状态更新时间
     */
    private String updateTime;

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public double getCommission() {
        return commission;
    }

    public void setCommission(double commission) {
        this.commission = commission;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getIssuingBankName() {
        return issuingBankName;
    }

    public void setIssuingBankName(String issuingBankName) {
        this.issuingBankName = issuingBankName;
    }

    public double getMoney() {
        return money;
    }

    public void setMoney(double money) {
        this.money = money;
    }

    public String getSelfOrderId() {
        return selfOrderId;
    }

    public void setSelfOrderId(String selfOrderId) {
        this.selfOrderId = selfOrderId;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public static boolean isTransfer(int status) {
        return status > START_TRADE;
    }

    @Override
    public String toString() {
        return "WithDrawRecordInfo{" +
                "cardNumber='" + cardNumber + '\'' +
                ", commission=" + commission +
                ", createTime='" + createTime + '\'' +
                ", id=" + id +
                ", issuingBankName='" + issuingBankName + '\'' +
                ", money=" + money +
                ", selfOrderId='" + selfOrderId + '\'' +
                ", status=" + status +
                ", updateTime='" + updateTime + '\'' +
                '}';
    }
}
