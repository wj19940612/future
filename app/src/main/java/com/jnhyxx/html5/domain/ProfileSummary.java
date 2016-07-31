package com.jnhyxx.html5.domain;

import java.io.Serializable;

public class ProfileSummary implements Serializable{

    /**
     * bankNum : null
     * userStatus : 0
     * bankStatus : 0
     * idCardNum :
     * couponCount : 0
     * bankName : null
     * userName :
     * tele : 178****7906
     * headPic :
     * provName : null
     * cityName : null
     * branName : null
     */

    private String bankNum;
    private int userStatus;
    private int bankStatus;
    private String idCardNum;
    private int couponCount;
    private String bankName;
    private String userName;
    private String tele;
    private String headPic;
    private String provName;
    private String cityName;
    private String branName;

    public String getBankNum() {
        return bankNum;
    }

    public void setBankNum(String bankNum) {
        this.bankNum = bankNum;
    }

    public int getUserStatus() {
        return userStatus;
    }

    public void setUserStatus(int userStatus) {
        this.userStatus = userStatus;
    }

    public int getBankStatus() {
        return bankStatus;
    }

    public void setBankStatus(int bankStatus) {
        this.bankStatus = bankStatus;
    }

    public String getIdCardNum() {
        return idCardNum;
    }

    public void setIdCardNum(String idCardNum) {
        this.idCardNum = idCardNum;
    }

    public int getCouponCount() {
        return couponCount;
    }

    public void setCouponCount(int couponCount) {
        this.couponCount = couponCount;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getTele() {
        return tele;
    }

    public void setTele(String tele) {
        this.tele = tele;
    }

    public String getHeadPic() {
        return headPic;
    }

    public void setHeadPic(String headPic) {
        this.headPic = headPic;
    }

    public String getProvName() {
        return provName;
    }

    public void setProvName(String provName) {
        this.provName = provName;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getBranName() {
        return branName;
    }

    public void setBranName(String branName) {
        this.branName = branName;
    }

    public NameAuth createNameAuth() {
        return new NameAuth(getUserStatus(), getUserName(), getIdCardNum());
    }

    public BankcardAuth createBankcardAuth() {
        return new BankcardAuth(getBankNum(), getProvName(), getCityName(), getTele(),
                getBranName(), getBankName(), -1, getBankStatus());
    }
}
