package com.jnhyxx.html5.domain.account;

import java.io.Serializable;

/**
 * Created by ${wangJie} on 2016/9/13.
 * 我的界面/提现界面/提款记录的model
 */

public class WithdrawRecord implements Serializable {
    private static final long serialVersionUID = -470395285557119048L;

    //提现
    public static final int RECORD_TYPE_WITHDRAW = -1;
    //充值
    public static final int RECORD_TYPE_RECHARGE = 1;

    /**
     * 充提状态，充提均使用该字段 0表示刚刚发起（充提）1表示审批通过（提现）2表示转账中（提现）3表示充提成功（充提）4表示提现拒绝（提现）5表示转账失败（提现',
     */
    //冲提刚刚发起
    public static final int WITHDRAW_AND_RECHARGE_INITIATE = 0;
    //提现审批通过
    public static final int WITHDRAW_PASS = 1;
    //提现转账中
    public static final int TRANSFERING = 2;
    //充提成功
    public static final int WITHDRAW_RECHARGE_SUCCESS = 3;
    //提现拒绝
    public static final int WITHDRAW_REFUSE = 4;
    //提现失败
    public static final int WITHDRAW_FAIL = 5;


    /**
     * commission : 0
     * createTime : 2016-08-19 14:14:05
     * id : 162
     * money : 0.01
     * remark : 用户提现
     * selfOrderId : tx371471587248967
     * status : 0
     * type : -1
     * typeDetail : -1001
     * updateTime : 2016-08-19 14:14:05
     * userId : 37
     */
    /**
     * '根据各平台计算得出本次手续费',
     */
    private double commission;
    /**
     * 流水id
     */
    private int id;
    /**
     * '充提金额',
     */
    private double money;
    /**
     * '充提类型描述',
     */
    private String remark;
    /**
     * '内部订单id，用于和第三方对接时做唯一标识',
     */
    private String selfOrderId;
    /**
     * '充提状态，充提均使用该字段 0表示刚刚发起（充提）1表示审批通过（提现）2表示转账中（提现）3表示充提成功（充提）4表示提现拒绝（提现）5表示转账失败（提现',
     */
    private int status;
    /**
     * '-1提现1充值',
     */
    private int type;
    /**
     * '充值提现具体类型',
     */
    private int typeDetail;
    private String updateTime;
    private String createTime;

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

    public double getMoney() {
        return money;
    }

    public void setMoney(double money) {
        this.money = money;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
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

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getTypeDetail() {
        return typeDetail;
    }

    public void setTypeDetail(int typeDetail) {
        this.typeDetail = typeDetail;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    @Override
    public String toString() {
        return "WithdrawRecord{" +
                "commission=" + commission +
                ", id=" + id +
                ", money=" + money +
                ", remark='" + remark + '\'' +
                ", selfOrderId='" + selfOrderId + '\'' +
                ", status=" + status +
                ", type=" + type +
                ", typeDetail=" + typeDetail +
                ", updateTime='" + updateTime + '\'' +
                ", createTime='" + createTime + '\'' +
                '}';
    }
}
