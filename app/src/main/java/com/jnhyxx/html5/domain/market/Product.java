package com.jnhyxx.html5.domain.market;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class Product implements Serializable, Parcelable {

    public static final String EX_PRODUCT = "product";
    public static final String EX_FUND_TYPE = "fund";
    public static final String EX_PRODUCT_LIST = "productList";

    public static final int FUND_TYPE_CASH = 0;
    public static final int FUND_TYPE_SCORE = 1;

    public static final String CURRENCY_RMB = "CNY";

    public static final int MARKET_STATUS_CLOSE = 0;
    public static final int MARKET_STATUS_OPEN = 1;

    public static final int TAG_NEW = 2;
    public static final int TAG_HOT = 1;
    public static final int TAG_NONE = 0;

    /**
     * id : 1002
     * imgs : http://jzstock.oss-cn-hangzhou.aliyuncs.com/2016-04-29_marketCL.png
     * marketCode : CME
     * commodityName : 美原油
     * instrumentID : CL1609
     * instrumentCode : CL
     * currency : USD
     * currencyName : 美元
     * currencySign : $
     * currencyUnit : 美元
     * multiple : 1000.0
     * decimalPlaces : 2
     * commodityDesc :
     * advertisement : 800元一手，炒原油
     * vendibility : 1
     * tag : 1
     * timeTag : 0
     * marketId : 14
     * marketName : 芝加哥商品交易所1
     * marketStatus : 1
     * baseline : 6
     * interval : 0.5
     * isDoule : 1
     * scale : 3.0
     * timeAndNum : 06:00/1381;05:00
     * nightTimeAndNum :
     * timeline : 06:00;05:00;
     * loddyType : null
     * accountCode : cainiu;score
     * minPrice : 0.01
     * rate : 6.6
     */

    private int id;
    private String imgs;
    private String marketCode;
    private String commodityName;
    private String instrumentID;
    private String instrumentCode;
    private String currency;
    private String currencyName;
    private String currencySign;
    private String currencyUnit;
    private double multiple;
    private int decimalPlaces;
    private String commodityDesc;
    private String advertisement;
    private int vendibility;
    private int tag;
    private int timeTag;
    private int marketId;
    private String marketName;
    private int marketStatus;
    private int baseline;
    private double interval;
    private int isDoule;
    private double scale;
    private String timeAndNum;
    private String nightTimeAndNum;
    private String timeline;
    private int loddyType;
    private String accountCode;
    private double minPrice;
    private double rate;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getImgs() {
        return imgs;
    }

    public void setImgs(String imgs) {
        this.imgs = imgs;
    }

    public String getMarketCode() {
        return marketCode;
    }

    public void setMarketCode(String marketCode) {
        this.marketCode = marketCode;
    }

    public String getCommodityName() {
        return commodityName;
    }

    public void setCommodityName(String commodityName) {
        this.commodityName = commodityName;
    }

    public String getInstrumentID() {
        return instrumentID;
    }

    public void setInstrumentID(String instrumentID) {
        this.instrumentID = instrumentID;
    }

    public String getInstrumentCode() {
        return instrumentCode;
    }

    public void setInstrumentCode(String instrumentCode) {
        this.instrumentCode = instrumentCode;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getCurrencyName() {
        return currencyName;
    }

    public void setCurrencyName(String currencyName) {
        this.currencyName = currencyName;
    }

    public String getCurrencySign() {
        return currencySign;
    }

    public void setCurrencySign(String currencySign) {
        this.currencySign = currencySign;
    }

    public String getCurrencyUnit() {
        return currencyUnit;
    }

    public void setCurrencyUnit(String currencyUnit) {
        this.currencyUnit = currencyUnit;
    }

    public double getMultiple() {
        return multiple;
    }

    public void setMultiple(double multiple) {
        this.multiple = multiple;
    }

    public int getDecimalPlaces() {
        return decimalPlaces;
    }

    public void setDecimalPlaces(int decimalPlaces) {
        this.decimalPlaces = decimalPlaces;
    }

    public String getCommodityDesc() {
        return commodityDesc;
    }

    public void setCommodityDesc(String commodityDesc) {
        this.commodityDesc = commodityDesc;
    }

    public String getAdvertisement() {
        return advertisement;
    }

    public void setAdvertisement(String advertisement) {
        this.advertisement = advertisement;
    }

    public int getVendibility() {
        return vendibility;
    }

    public void setVendibility(int vendibility) {
        this.vendibility = vendibility;
    }

    public int getTag() {
        return tag;
    }

    public void setTag(int tag) {
        this.tag = tag;
    }

    public int getTimeTag() {
        return timeTag;
    }

    public void setTimeTag(int timeTag) {
        this.timeTag = timeTag;
    }

    public int getMarketId() {
        return marketId;
    }

    public void setMarketId(int marketId) {
        this.marketId = marketId;
    }

    public String getMarketName() {
        return marketName;
    }

    public void setMarketName(String marketName) {
        this.marketName = marketName;
    }

    public int getMarketStatus() {
        return marketStatus;
    }

    public void setMarketStatus(int marketStatus) {
        this.marketStatus = marketStatus;
    }

    public int getBaseline() {
        return baseline;
    }

    public void setBaseline(int baseline) {
        this.baseline = baseline;
    }

    public double getInterval() {
        return interval;
    }

    public void setInterval(double interval) {
        this.interval = interval;
    }

    public int getIsDoule() {
        return isDoule;
    }

    public void setIsDoule(int isDoule) {
        this.isDoule = isDoule;
    }

    public double getScale() {
        return scale;
    }

    public void setScale(double scale) {
        this.scale = scale;
    }

    public String getTimeAndNum() {
        return timeAndNum;
    }

    public void setTimeAndNum(String timeAndNum) {
        this.timeAndNum = timeAndNum;
    }

    public String getNightTimeAndNum() {
        return nightTimeAndNum;
    }

    public void setNightTimeAndNum(String nightTimeAndNum) {
        this.nightTimeAndNum = nightTimeAndNum;
    }

    public String getTimeline() {
        return timeline;
    }

    public void setTimeline(String timeline) {
        this.timeline = timeline;
    }

    public int getLoddyType() {
        return loddyType;
    }

    public void setLoddyType(int loddyType) {
        this.loddyType = loddyType;
    }

    public String getAccountCode() {
        return accountCode;
    }

    public void setAccountCode(String accountCode) {
        this.accountCode = accountCode;
    }

    public double getMinPrice() {
        return minPrice;
    }

    public void setMinPrice(double minPrice) {
        this.minPrice = minPrice;
    }

    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }

    public int getLossProfitPrecision() {
        return calLossProfitPrecision(multiple / Math.pow(10, decimalPlaces));
    }

    private int calLossProfitPrecision(double v) {
        String s = String.valueOf(v);
        int indexOfPoint = s.indexOf(".");
        if (indexOfPoint > -1) {
            s = s.replaceAll("0+?$", ""); // remove all tail 0
            return s.substring(indexOfPoint + 1).length();
        }
        return 0;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.imgs);
        dest.writeString(this.marketCode);
        dest.writeString(this.commodityName);
        dest.writeString(this.instrumentID);
        dest.writeString(this.instrumentCode);
        dest.writeString(this.currency);
        dest.writeString(this.currencyName);
        dest.writeString(this.currencySign);
        dest.writeString(this.currencyUnit);
        dest.writeDouble(this.multiple);
        dest.writeInt(this.decimalPlaces);
        dest.writeString(this.commodityDesc);
        dest.writeString(this.advertisement);
        dest.writeInt(this.vendibility);
        dest.writeInt(this.tag);
        dest.writeInt(this.timeTag);
        dest.writeInt(this.marketId);
        dest.writeString(this.marketName);
        dest.writeInt(this.marketStatus);
        dest.writeInt(this.baseline);
        dest.writeDouble(this.interval);
        dest.writeInt(this.isDoule);
        dest.writeDouble(this.scale);
        dest.writeString(this.timeAndNum);
        dest.writeString(this.nightTimeAndNum);
        dest.writeString(this.timeline);
        dest.writeInt(this.loddyType);
        dest.writeString(this.accountCode);
        dest.writeDouble(this.minPrice);
        dest.writeDouble(this.rate);
    }

    public Product() {
    }

    protected Product(Parcel in) {
        this.id = in.readInt();
        this.imgs = in.readString();
        this.marketCode = in.readString();
        this.commodityName = in.readString();
        this.instrumentID = in.readString();
        this.instrumentCode = in.readString();
        this.currency = in.readString();
        this.currencyName = in.readString();
        this.currencySign = in.readString();
        this.currencyUnit = in.readString();
        this.multiple = in.readDouble();
        this.decimalPlaces = in.readInt();
        this.commodityDesc = in.readString();
        this.advertisement = in.readString();
        this.vendibility = in.readInt();
        this.tag = in.readInt();
        this.timeTag = in.readInt();
        this.marketId = in.readInt();
        this.marketName = in.readString();
        this.marketStatus = in.readInt();
        this.baseline = in.readInt();
        this.interval = in.readDouble();
        this.isDoule = in.readInt();
        this.scale = in.readDouble();
        this.timeAndNum = in.readString();
        this.nightTimeAndNum = in.readString();
        this.timeline = in.readString();
        this.loddyType = in.readInt();
        this.accountCode = in.readString();
        this.minPrice = in.readDouble();
        this.rate = in.readDouble();
    }

    public static final Parcelable.Creator<Product> CREATOR = new Parcelable.Creator<Product>() {
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
