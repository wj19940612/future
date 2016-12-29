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
     * idStatus实名状态 0未填写，1已填写，2已认证
     */
    public static final int REAL_NAME_STATUS_UNFILLED = 0;
    public static final int REAL_NAME_STATUS_FILLED = 1;
    public static final int REAL_NAME_STATUS_VERIFIED = 2;

    /**
     * birthday : 2222
     * certificationStatus : 1
     * introduction : 小师傅
     * land : 安徽
     * realName : yingying
     * userName : 正
     * userPortrait : https://hystock.oss-cn-qingdao.aliyuncs.com/ueditor/1480048019943.png
     * userSex : 1
     */

    private String birthday;
    //用户实名认证状态 0=未认证 1=已填写 2=已认证
    private int certificationStatus;
    //个人简介
    private String introduction;
    private String land;
    private String realName;
    private String userName;
    //用户头像地址
    private String userPortrait;
    //（0是女，1是男）
    private int userSex;

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

    public int getUserSex() {
        return userSex;
    }

    public void setUserSex(int userSex) {
        this.userSex = userSex;
    }

    public boolean isMan() {
        if (getUserSex() == SEX_MAN) {
            return true;
        }
        return false;
    }

}
