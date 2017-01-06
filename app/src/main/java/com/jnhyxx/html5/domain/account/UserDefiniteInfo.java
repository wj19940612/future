package com.jnhyxx.html5.domain.account;

/**
 * Created by ${wangJie} on 2016/12/29.
 * 用户信息界面的model
 * 包含用户的头像，性别等信息
 */

public class UserDefiniteInfo {

    //性别的表示 （0是女，1是男）
    private static final int SEX_MAN = 1;

    /**
     * certificationStatus实名状态 0未填写，1已填写，2已认证
     */
    public static final int REAL_NAME_STATUS_UNFILLED = 0;
    public static final int REAL_NAME_STATUS_FILLED = 1;
    public static final int REAL_NAME_STATUS_VERIFIED = 2;
    /**
     * birthday : 2017-3-4
     * certificationStatus : 2
     * chinaSex : 女
     * introduction : 家里蹲大学减肥快乐撒解放路科技大厦独守空房了解萨达六块腹肌代
     * land : 陕西省-西安市
     * realName : 王杰
     * userName : 昵称123abc10
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
    //真实姓名
    private String realName;
    //昵称
    private String userName;
    //头像网址
    private String userPortrait;


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

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPortrait() {
        return userPortrait;
    }

    public void setUserPortrait(String userPortrait) {
        this.userPortrait = userPortrait;
    }

    @Override
    public String toString() {
        return "UserDefiniteInfo{" +
                "birthday='" + birthday + '\'' +
                ", certificationStatus=" + certificationStatus +
                ", chinaSex='" + chinaSex + '\'' +
                ", introduction='" + introduction + '\'' +
                ", land='" + land + '\'' +
                ", realName='" + realName + '\'' +
                ", userName='" + userName + '\'' +
                ", userPortrait='" + userPortrait + '\'' +
                '}';
    }
}
