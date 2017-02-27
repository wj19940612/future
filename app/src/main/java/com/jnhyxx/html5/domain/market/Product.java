package com.jnhyxx.html5.domain.market;

import android.os.Parcel;
import android.os.Parcelable;

public class Product implements Parcelable {

    public static final String EX_PRODUCT = "product";
    public static final String EX_FUND_TYPE = "fund";
    public static final String EX_PRODUCT_LIST = "productList";

    /**
     * 1  实盘
     * 0  金币，模拟交易
     */
    public static final int FUND_TYPE_CASH = 1;
    public static final int FUND_TYPE_SIMULATION = 0;

    public static final int MARKET_STATUS_CLOSE = 0;
    public static final int MARKET_STATUS_OPEN = 1;

    public static final int TAG_NEW = 1;
    public static final int TAG_HOT = 2;
    public static final int TAG_NONE = 0;

    private static final int IS_DOMESTIC = 1;
    public static final String VARIETY_TYPE_US_CRUDE = "CL";
    /**
     * displayMarketTimes : 06:00;07:00;04:58
     * decimalScale : 0.2
     * sign : $
     * flashChartPriceInterval : 20
     * varietyType : CL
     * baseline : 2
     * isDomestic : 0
     * tags : 0
     * exchangeId : 9
     * openMarketTime : 06:00;05:00
     * varietyId : 10
     * exchangeStatus : 1
     * contractsCode : CL1609
     * advertisement : 来吧
     * currency : USD
     * marketPoint : 2
     * varietyName : 美原油
     * eachPointMoney : 1000
     * currencyUnit : 美元
     * ratio : 6.65
     */

    private String displayMarketTimes;
    private double decimalScale;
    private String sign;
    private double flashChartPriceInterval;
    private String varietyType;
    private int baseline;
    // 1 国内品种  0 国际品种
    private int isDomestic;
    private int tags;
    private int exchangeId;
    private String openMarketTime;
    private int varietyId;
    private int exchangeStatus;
    private String contractsCode;
    private String currency;
    private int marketPoint;
    private String varietyName;
    private double eachPointMoney;
    private String currencyUnit;
    private double ratio;
    private String advertisement;

    // 额外的字段用于判断是否为可选的产品
    private boolean isOptional;

    public void setIsOptional(boolean isOptional) {
        this.isOptional = isOptional;
    }

    public boolean getIsOptional() {
        return isOptional;
    }

    public String getDisplayMarketTimes() {
        return displayMarketTimes;
    }

    public void setDisplayMarketTimes(String displayMarketTimes) {
        this.displayMarketTimes = displayMarketTimes;
    }

    public double getLimitUpPercent() {
        return decimalScale;
    }

    public void setLimitUpPercent(double limitUpPercent) {
        this.decimalScale = limitUpPercent;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public double getFlashChartPriceInterval() {
        return flashChartPriceInterval;
    }

    public void setFlashChartPriceInterval(double fcpInterVal) {
        this.flashChartPriceInterval = fcpInterVal;
    }

    public String getVarietyType() {
        return varietyType;
    }

    public void setVarietyType(String varietyType) {
        this.varietyType = varietyType;
    }

    public int getBaseline() {
        return baseline;
    }

    public void setBaseline(int baseline) {
        this.baseline = baseline;
    }

    public boolean isDomestic() {
        return isDomestic == IS_DOMESTIC;
    }

    public boolean isForeign() {
        return isDomestic != IS_DOMESTIC;
    }

    public int getTags() {
        return tags;
    }

    public void setTags(int tags) {
        this.tags = tags;
    }

    public int getExchangeId() {
        return exchangeId;
    }

    public void setExchangeId(int exchangeId) {
        this.exchangeId = exchangeId;
    }

    public String getOpenMarketTime() {
        return openMarketTime;
    }

    public void setOpenMarketTime(String openMarketTime) {
        this.openMarketTime = openMarketTime;
    }

    public int getVarietyId() {
        return varietyId;
    }

    public void setVarietyId(int varietyId) {
        this.varietyId = varietyId;
    }

    public int getExchangeStatus() {
        return exchangeStatus;
    }

    public void setExchangeStatus(int exchangeStatus) {
        this.exchangeStatus = exchangeStatus;
    }

    public String getContractsCode() {
        return contractsCode;
    }

    public void setContractsCode(String contractsCode) {
        this.contractsCode = contractsCode;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public int getPriceDecimalScale() {
        return marketPoint;
    }

    public String getVarietyName() {
        return varietyName;
    }

    public void setVarietyName(String varietyName) {
        this.varietyName = varietyName;
    }

    public double getEachPointMoney() {
        return eachPointMoney;
    }

    public void setEachPointMoney(double eachPointMoney) {
        this.eachPointMoney = eachPointMoney;
    }

    public String getCurrencyUnit() {
        return currencyUnit;
    }

    public void setCurrencyUnit(String currencyUnit) {
        this.currencyUnit = currencyUnit;
    }

    public double getRatio() {
        return ratio;
    }

    public void setRatio(double ratio) {
        this.ratio = ratio;
    }

    public int getLossProfitScale() {
        return calLossProfitScale(eachPointMoney / Math.pow(10, getPriceDecimalScale()));
    }

    private int calLossProfitScale(double v) {
        String s = String.valueOf(v);
        int indexOfPoint = s.indexOf(".");
        if (indexOfPoint > -1) {
            s = s.replaceAll("0+?$", ""); // remove all tail 0
            return s.substring(indexOfPoint + 1).length();

        }
        return 0;
    }

    public String getAdvertisement() {
        return advertisement;
    }

    public void setAdvertisement(String advertisement) {
        this.advertisement = advertisement;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.displayMarketTimes);
        dest.writeDouble(this.decimalScale);
        dest.writeString(this.sign);
        dest.writeDouble(this.flashChartPriceInterval);
        dest.writeString(this.varietyType);
        dest.writeInt(this.baseline);
        dest.writeInt(this.isDomestic);
        dest.writeInt(this.tags);
        dest.writeInt(this.exchangeId);
        dest.writeString(this.openMarketTime);
        dest.writeInt(this.varietyId);
        dest.writeInt(this.exchangeStatus);
        dest.writeString(this.contractsCode);
        dest.writeString(this.currency);
        dest.writeInt(this.marketPoint);
        dest.writeString(this.varietyName);
        dest.writeDouble(this.eachPointMoney);
        dest.writeString(this.currencyUnit);
        dest.writeDouble(this.ratio);
        dest.writeString(this.advertisement);
    }

    public Product() {
    }

    protected Product(Parcel in) {
        this.displayMarketTimes = in.readString();
        this.decimalScale = in.readDouble();
        this.sign = in.readString();
        this.flashChartPriceInterval = in.readDouble();
        this.varietyType = in.readString();
        this.baseline = in.readInt();
        this.isDomestic = in.readInt();
        this.tags = in.readInt();
        this.exchangeId = in.readInt();
        this.openMarketTime = in.readString();
        this.varietyId = in.readInt();
        this.exchangeStatus = in.readInt();
        this.contractsCode = in.readString();
        this.currency = in.readString();
        this.marketPoint = in.readInt();
        this.varietyName = in.readString();
        this.eachPointMoney = in.readDouble();
        this.currencyUnit = in.readString();
        this.ratio = in.readDouble();
        this.advertisement = in.readString();
    }

    public static final Creator<Product> CREATOR = new Creator<Product>() {
        @Override
        public Product createFromParcel(Parcel source) {
            return new Product(source);
        }

        @Override
        public Product[] newArray(int size) {
            return new Product[size];
        }
    };
}
