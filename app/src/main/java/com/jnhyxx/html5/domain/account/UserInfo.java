package com.jnhyxx.html5.domain.account;

import android.text.TextUtils;

/**
 * Created by Administrator on 2016/8/31.
 * 登陆成功后返回的数据
 */

public class UserInfo {

    /**
     "issuingbankName": "中国工商银行",
     "idCard": "3301**********1314",
     "userPhone": "137****4454",
     "icon": "https://hystock.oss-cn-qingdao.aliyuncs.com/ueditor/1473647614249030116.png",
     "userName": "新昵称1",
     "moneyUsable": 105466.16,
     "idStatus": 2,
     "realName": "*斌",
     "bIsSetNickName": true,
     "scoreUsable": -1603300,
     "bankId": 1,
     "cardPhone": "13777804454",
     "cardState": 2,
     "id": 8,
     "cardNumber": "6666*********6666"
     */

    /**
     * 银行卡绑定状态  0未填写，1已填写，2已认证
     */
    public static final int BANKCARD_STATUS_UNFILLED = 0;
    public static final int BANKCARD_STATUS_FILLED = 1;
    public static final int BANKCARD_STATUS_BOUND = 2;

    /**
     * idStatus实名状态 0未填写，1已填写，2已认证
     */
    public static final int REAL_NAME_STATUS_UNFILLED = 0;
    public static final int REAL_NAME_STATUS_FILLED = 1;
    public static final int REAL_NAME_STATUS_VERIFIED = 2;

    /**
     * moneyUsable可用资金余额
     */
    private double moneyUsable;
    /**
     * scoreUsable可用积分余额
     */
    private double scoreUsable;
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
     * icon银行图标
     */
    private String icon;
    /**
     * bIsSetNickName是否修改过昵称 false未修改true已修改
     */
    private boolean bIsSetNickName;

    private int bankId;
    /**
     * birthday : 2017-3-4
     * certificationStatus : 2
     * chinaSex : 女
     * introduction : 家里蹲大学减肥快乐撒解放路科技大厦独守空房了解萨达六块腹肌代
     * land : 陕西省-西安市
     * userPortrait : https://hystock.oss-cn-qingdao.aliyuncs.com/ueditor/1482833394930.png
     * userSex : 0
     */

    //生日
    private String birthday;
    //认证状态。用户实名认证状态 0=未认证 1=已填写 2=已认证
    private int certificationStatus;
    //性别
    private String chinaSex;
    //简介
    private String introduction;
    //出生地
    private String land;
    //头像网址
    private String userPortrait;

    public void setUserDefiniteInfo(UserDefiniteInfo userDefiniteInfo) {
        setBirthday(userDefiniteInfo.getBirthday());
        setIdStatus(userDefiniteInfo.getCertificationStatus());
        setChinaSex(userDefiniteInfo.getChinaSex());
        setIntroduction(userDefiniteInfo.getIntroduction());
        setLand(userDefiniteInfo.getLand());
        setUserPortrait(userDefiniteInfo.getUserPortrait());
    }

    public UserDefiniteInfo getUserDefiniteInfo() {
        UserDefiniteInfo userDefiniteInfo = new UserDefiniteInfo();
        userDefiniteInfo.setBirthday(getBirthday());
        userDefiniteInfo.setChinaSex(getChinaSex());
        if (!TextUtils.isEmpty(getChinaSex())) {
            userDefiniteInfo.setUserSex(getChinaSex().equalsIgnoreCase("男") ? 1 : 0);
        }

        if (!isUserRealNameAuth()) {
            userDefiniteInfo.setRealName(getRealName());
        }
        userDefiniteInfo.setCertificationStatus(getIdStatus());
        userDefiniteInfo.setIntroduction(getIntroduction());
        userDefiniteInfo.setLand(getLand());
        userDefiniteInfo.setUserPortrait(getUserPortrait());
        return userDefiniteInfo;
    }

    public double getMoneyUsable() {
        return moneyUsable;
    }

    public void setMoneyUsable(double moneyUsable) {
        this.moneyUsable = moneyUsable;
    }

    public double getScoreUsable() {
        return scoreUsable;
    }

    public void setScoreUsable(double scoreUsable) {
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

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public int getCertificationStatus() {
        return certificationStatus;
    }

    public void setCertificationStatus(int certificationStatus) {
        this.certificationStatus = certificationStatus;
    }

    public String getChinaSex() {
        return chinaSex;
    }

    public void setChinaSex(String chinaSex) {
        this.chinaSex = chinaSex;
    }

    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public String getLand() {
        return land;
    }

    public void setLand(String land) {
        this.land = land;
    }

    public String getUserPortrait() {
        return userPortrait;
    }

    public void setUserPortrait(String userPortrait) {
        this.userPortrait = userPortrait;
    }

    public boolean isUserRealNameAuth() {
        if (getIdStatus() == REAL_NAME_STATUS_VERIFIED) {
            return true;
        }
        return false;
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
                ", birthday='" + birthday + '\'' +
                ", certificationStatus=" + certificationStatus +
                ", chinaSex='" + chinaSex + '\'' +
                ", introduction='" + introduction + '\'' +
                ", land='" + land + '\'' +
                ", userPortrait='" + userPortrait + '\'' +
                '}';
    }
}
