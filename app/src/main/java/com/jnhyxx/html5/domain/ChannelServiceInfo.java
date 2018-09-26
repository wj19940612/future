package com.jnhyxx.html5.domain;

/**
 * Created by ${wangJie} on 2016/10/21.
 * 客服电话和qq
 */

public class ChannelServiceInfo {

    public static final int QQ_TYPE_MARKETING = 1;
    public static final int QQ_TYPE_NORMAL = 2;

    /**
     * backstageDomain : newtest.jnhyxx.com
     * channelQrCode : https://hystock.oss-cn-qingdao.aliyuncs.com/ueditor/1474959842201066110.png
     * description : 二级渠道h5访问
     * domain : newtest.jnhyxx.com
     * id : 25
     * level : 2
     * name : 聚光湖北
     * phone : 0571111111
     * productName : 产品测试
     * qq : 800186640
     * qqType : 1
     * sign : 操盘手
     * superId : 12
     * userNamePrefix : 聚光湖北
     */
    private String backstageDomain;
    private String channelQrCode;
    private String description;
    private String domain;
    private int id;
    private int level;
    private String name;
    private String phone;
    private String productName;
    private String qq;
    private int qqType;
    private String sign;
    private int superId;
    private String userNamePrefix;

    public String getBackstageDomain() {
        return backstageDomain;
    }

    public void setBackstageDomain(String backstageDomain) {
        this.backstageDomain = backstageDomain;
    }

    public String getChannelQrCode() {
        return channelQrCode;
    }

    public void setChannelQrCode(String channelQrCode) {
        this.channelQrCode = channelQrCode;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getQq() {
        return qq;
    }

    public void setQq(String qq) {
        this.qq = qq;
    }

    public int getQqType() {
        return qqType;
    }

    public void setQqType(int qqType) {
        this.qqType = qqType;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public int getSuperId() {
        return superId;
    }

    public void setSuperId(int superId) {
        this.superId = superId;
    }

    public String getUserNamePrefix() {
        return userNamePrefix;
    }

    public void setUserNamePrefix(String userNamePrefix) {
        this.userNamePrefix = userNamePrefix;
    }

    @Override
    public String toString() {
        return "ChannelServiceInfo{" +
                "backstageDomain='" + backstageDomain + '\'' +
                ", channelQrCode='" + channelQrCode + '\'' +
                ", description='" + description + '\'' +
                ", domain='" + domain + '\'' +
                ", id=" + id +
                ", level=" + level +
                ", name='" + name + '\'' +
                ", phone='" + phone + '\'' +
                ", productName='" + productName + '\'' +
                ", qq='" + qq + '\'' +
                ", qqType=" + qqType +
                ", sign='" + sign + '\'' +
                ", superId=" + superId +
                ", userNamePrefix='" + userNamePrefix + '\'' +
                '}';
    }
}
