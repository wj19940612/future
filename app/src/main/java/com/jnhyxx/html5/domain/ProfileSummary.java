package com.jnhyxx.html5.domain;

public class ProfileSummary {

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
     */

    private Object bankNum;
    private int userStatus;
    private int bankStatus;
    private String idCardNum;
    private int couponCount;
    private Object bankName;
    private String userName;
    private String tele;
    private String headPic;

    public Object getBankNum() {
        return bankNum;
    }

    public void setBankNum(Object bankNum) {
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

    public Object getBankName() {
        return bankName;
    }

    public void setBankName(Object bankName) {
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
}
