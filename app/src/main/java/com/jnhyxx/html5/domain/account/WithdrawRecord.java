package com.jnhyxx.html5.domain.account;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ${wangJie} on 2016/9/13.
 * 我的界面/提现界面/提款记录的model
 */

public class WithdrawRecord implements Serializable {
    private static final long serialVersionUID = -470395285557119048L;

    //提现
    public static final int RECORD_TYPE_WITHDRAW =-1 ;
    //充值
    public static final int RECORD_TYPE_RECHARGE = 1;



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
    private int commission;
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
    private int userId;

    public static WithdrawRecord objectFromData(String str) {

        return new Gson().fromJson(str, WithdrawRecord.class);
    }

    public static WithdrawRecord objectFromData(String str, String key) {

        try {
            JSONObject jsonObject = new JSONObject(str);

            return new Gson().fromJson(jsonObject.getString(str), WithdrawRecord.class);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static List<WithdrawRecord> arrayWithdrawRecordFromData(String str) {

        Type listType = new TypeToken<ArrayList<WithdrawRecord>>() {
        }.getType();

        return new Gson().fromJson(str, listType);
    }

    public static List<WithdrawRecord> arrayWithdrawRecordFromData(String str, String key) {

        try {
            JSONObject jsonObject = new JSONObject(str);
            Type listType = new TypeToken<ArrayList<WithdrawRecord>>() {
            }.getType();

            return new Gson().fromJson(jsonObject.getString(str), listType);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return new ArrayList();


    }

    public int getCommission() {
        return commission;
    }

    public void setCommission(int commission) {
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

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}
