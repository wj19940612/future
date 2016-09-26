package com.jnhyxx.html5.domain.account;

/**
 * Created by Administrator on 2016/8/31.
 * 登陆成功后返回的数据
 */

public class UserInfo {

    /**
     * 银行卡绑定状态  0未填写，1已填写，2已认证
     */
    public static final int BANK_CARD_AUTH_STATUS_NOT_WRITE = 0;
    public static final int BANK_CARD_AUTH_STATUS_WRITE = 1;
    public static final int BANK_CARD_AUTH_STATUS_ATTESTATION = 2;

    /**
     * idStatus实名状态 0未填写，1已填写，2已认证
     */
    public static final int REAL_NAME_AUTH_STATUS_NOT_WRITE = 0;
    public static final int REAL_NAME_AUTH_STATUS_WRITE = 1;
    public static final int REAL_NAME_AUTH_STATUS_ATTESTATION = 2;


    // TODO: 2016/9/23 冲突的数据，可能没有问题 
   /* public static final int ID_STATUS_STATUS_FILL = 1;
    public static final int ID_STATUS_STATUS_AUTHERIZED = 2;

    *//**
     * idStatus实名状态 0未填写，1已填写，2已认证
     *//*
    public static final int BANKCARD_STATUS_FILL = 1;
    public static final int BANKCARD_STATUS_AUTHERIZED = 2;*/


    /**
     * moneyUsable可用资金余额
     */
    private double moneyUsable;
    /**
     * scoreUsable可用积分余额
     */
    private int scoreUsable;
    /**
     * userName用户昵称
     */
    private String userName;
    /**
     * userPhone用户手机号
     */
    private String userPhone;
    /**
     * idStatus实名状态 0未填写，1已填写，2已认证
     */
    private int idStatus;
    /**
     * realName实名
     */
    private String realName;
    /**
     * cardPhone银行卡对应的手机号
     */
    private String cardPhone;
    /**
     * issuingbankName银行名
     */
    private String issuingbankName;
    /**
     * idCard身份证号
     */
    private String idCard;
    /**
     * cardState银行卡状态 0未填写，1已填写，2已认证
     */
    private int cardState;
    private int id;
    /**
     * cardNumber银行卡号
     */
    private String cardNumber;
    /**
     * icon : https://hystock.oss-cn-qingdao.aliyuncs.com/ueditor/1473647868883060881.png
     * bIsSetNickName : false
     * bankId : 3
     */
    /**
     * icon银行图标
     */
    private String icon;
    /**
     * bIsSetNickName是否修改过昵称 false未修改true已修改
     */
    private boolean bIsSetNickName;
    private int bankId;

    public double getMoneyUsable() {
        return moneyUsable;
    }

    public void setMoneyUsable(double moneyUsable) {
        this.moneyUsable = moneyUsable;
    }

    public int getScoreUsable() {
        return scoreUsable;
    }

    public void setScoreUsable(int scoreUsable) {
        this.scoreUsable = scoreUsable;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    public int getIdStatus() {
        return idStatus;
    }

    public void setIdStatus(int idStatus) {
        this.idStatus = idStatus;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getCardPhone() {
        return cardPhone;
    }

    public void setCardPhone(String cardPhone) {
        this.cardPhone = cardPhone;
    }

    public String getIssuingbankName() {
        return issuingbankName;
    }

    public void setIssuingbankName(String issuingbankName) {
        this.issuingbankName = issuingbankName;
    }

    public String getIdCard() {
        return idCard;
    }

    public void setIdCard(String idCard) {
        this.idCard = idCard;
    }

    public int getCardState() {
        return cardState;
    }

    public void setCardState(int cardState) {
        this.cardState = cardState;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public boolean isNickNameModifiedBefore() {
        return bIsSetNickName;
    }

    public void setNickNameModified() {
        this.bIsSetNickName = true;
    }

    public int getBankId() {
        return bankId;
    }

    public void setBankId(int bankId) {
        this.bankId = bankId;
    }

    @Override
    public String toString() {
        return "UserInfo{" +
                "moneyUsable=" + moneyUsable +
                ", scoreUsable=" + scoreUsable +
                ", userName='" + userName + '\'' +
                ", userPhone='" + userPhone + '\'' +
                ", idStatus=" + idStatus +
                ", realName='" + realName + '\'' +
                ", cardPhone='" + cardPhone + '\'' +
                ", issuingbankName='" + issuingbankName + '\'' +
                ", idCard='" + idCard + '\'' +
                ", cardState=" + cardState +
                ", id=" + id +
                ", cardNumber='" + cardNumber + '\'' +
                ", icon='" + icon + '\'' +
                ", bIsSetNickName=" + bIsSetNickName +
                ", bankId=" + bankId +
                '}';
    }
}
