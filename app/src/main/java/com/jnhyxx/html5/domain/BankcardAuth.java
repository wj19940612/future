package com.jnhyxx.html5.domain;

public class BankcardAuth {

    public static final int STATUS_NOT_FILLED = 0;
    public static final int STATUS_BE_BOUND = 1;
    public static final int STATUS_FILLED = 2;

    /**
     * bankNum : 6225885866059181
     * provName : 黑龙江省
     * cityName : 大庆市
     * phone : 13567124531
     * branName :
     * bankName : 招商银行
     * id : 174
     * status : 2
     */

    private String bankNum;
    private String provName;
    private String cityName;
    private String phone;
    private String branName;
    private String bankName;
    private int id;
    private int status;

    public String getBankNum() {
        return bankNum;
    }

    public void setBankNum(String bankNum) {
        this.bankNum = bankNum;
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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getBranName() {
        return branName;
    }

    public void setBranName(String branName) {
        this.branName = branName;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getHiddenSummary() {
        String bankNum = getBankNum();
        int length = bankNum.length();
        return getBankName() + "*" + bankNum.substring(length - 4);
    }
}
