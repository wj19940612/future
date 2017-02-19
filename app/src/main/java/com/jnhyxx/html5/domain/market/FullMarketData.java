package com.jnhyxx.html5.domain.market;

import android.os.Parcel;
import android.os.Parcelable;

public class FullMarketData implements Parcelable {

    public static final String EX_MARKET_DATA = "marketData";

    /**
     * askPrice : 45.14
     * askVolume : 24
     * bidPrice : 45.13
     * bidVolume : 14
     * highestPrice : 45.66
     * instrumentId : CL1610
     * lastPrice : 45.13
     * lowestPrice : 44.85
     * openPrice : 45.57
     * preClsPrice : 45.88
     * preSetPrice : 45.88
     * settlePrice : 45.88
     * tradeDay : 2016-09-12
     * upDropPrice : -0.75
     * upDropSpeed : -0.02
     * upTime : 1473669891736
     * positionVolume 今日持仓量
     * prePositionVolume 昨日持仓量
     *
     * 内盘新增:
     * downLimitPrice: 2621,
     * upLimitPrice: 2956,
     * exchangeId:
     * openInterest: 1399424,
     * preOpenInterest: 1399424,
     * turnover: 2.468655404E+10,
     * upTimeFormat: "2016-11-16 22:13:15.000",
     * volume : 296902
     *
     */

    private double askPrice; // 卖一价
    private int askVolume; // 卖一量
    private double bidPrice; // 买一价
    private int bidVolume; // 买一量
    private double highestPrice; //当日最高价
    private String instrumentId; // 合约代码
    private double lastPrice;
    private double lowestPrice; //当日最低价
    private double openPrice; //开盘价
    private double preClsPrice; //昨日收盘价
    private double preSetPrice; //昨日结算价
    private double settlePrice; //结算价
    private String tradeDay; // 交易日
    private double upDropPrice; //涨跌值
    private double upDropSpeed; //涨跌幅
    private long upTime; // 行情时间戳
    private long positionVolume;//今日持仓量
    private long prePositionVolume;//昨日持仓量

    private double downLimitPrice; //跌停板
    private double upLimitPrice; //涨停板
    private String exchangeId;
    private long openInterest; //今日持仓量
    private long preOpenInterest; //昨日持仓量
    private double turnover; //成交金额
    private String upTimeFormat;
    private long volume; //成交量

    public double getAskPrice() {
        return askPrice;
    }

    public int getAskVolume() {
        return askVolume;
    }

    public double getBidPrice() {
        return bidPrice;
    }

    public int getBidVolume() {
        return bidVolume;
    }

    public double getHighestPrice() {
        return highestPrice;
    }

    public String getInstrumentId() {
        return instrumentId;
    }

    public double getLastPrice() {
        return lastPrice;
    }

    public void setLastPrice(double lastPrice) {
        this.lastPrice = lastPrice;
    }

    public void setAskVolume(int askVolume) {
        this.askVolume = askVolume;
    }

    public void setBidVolume(int bidVolume) {
        this.bidVolume = bidVolume;
    }

    public double getLowestPrice() {
        return lowestPrice;
    }

    public double getOpenPrice() {
        return openPrice;
    }

    public double getPreClsPrice() {
        return preClsPrice;
    }

    public double getPreSetPrice() {
        return preSetPrice;
    }

    public double getSettlePrice() {
        return settlePrice;
    }

    public long getPositionVolume() {
        return positionVolume;
    }

    public long getPrePositionVolume() {
        return prePositionVolume;
    }

    public long getOpenInterest() {
        return openInterest;
    }

    public long getPreOpenInterest() {
        return preOpenInterest;
    }

    public long getVolume() {
        return volume;
    }

    public double getTurnover() {
        return turnover;
    }

    public double getDownLimitPrice() {
        return downLimitPrice;
    }

    public double getUpLimitPrice() {
        return upLimitPrice;
    }

    @Override
    public String toString() {
        return "FullMarketData{" +
                "askPrice=" + askPrice +
                ", askVolume=" + askVolume +
                ", bidPrice=" + bidPrice +
                ", bidVolume=" + bidVolume +
                ", highestPrice=" + highestPrice +
                ", instrumentId='" + instrumentId + '\'' +
                ", lastPrice=" + lastPrice +
                ", lowestPrice=" + lowestPrice +
                ", openPrice=" + openPrice +
                ", preClsPrice=" + preClsPrice +
                ", preSetPrice=" + preSetPrice +
                ", settlePrice=" + settlePrice +
                ", tradeDay='" + tradeDay + '\'' +
                ", upDropPrice=" + upDropPrice +
                ", upDropSpeed=" + upDropSpeed +
                ", upTime=" + upTime +
                ", positionVolume=" + positionVolume +
                ", prePositionVolume=" + prePositionVolume +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(this.askPrice);
        dest.writeInt(this.askVolume);
        dest.writeDouble(this.bidPrice);
        dest.writeInt(this.bidVolume);
        dest.writeDouble(this.highestPrice);
        dest.writeString(this.instrumentId);
        dest.writeDouble(this.lastPrice);
        dest.writeDouble(this.lowestPrice);
        dest.writeDouble(this.openPrice);
        dest.writeDouble(this.preClsPrice);
        dest.writeDouble(this.preSetPrice);
        dest.writeDouble(this.settlePrice);
        dest.writeString(this.tradeDay);
        dest.writeDouble(this.upDropPrice);
        dest.writeDouble(this.upDropSpeed);
        dest.writeLong(this.upTime);
        dest.writeLong(this.positionVolume);
        dest.writeLong(this.prePositionVolume);
        dest.writeDouble(this.downLimitPrice);
        dest.writeDouble(this.upLimitPrice);
        dest.writeString(this.exchangeId);
        dest.writeLong(this.openInterest);
        dest.writeLong(this.preOpenInterest);
        dest.writeDouble(this.turnover);
        dest.writeString(this.upTimeFormat);
        dest.writeLong(this.volume);
    }

    public FullMarketData() {
    }

    protected FullMarketData(Parcel in) {
        this.askPrice = in.readDouble();
        this.askVolume = in.readInt();
        this.bidPrice = in.readDouble();
        this.bidVolume = in.readInt();
        this.highestPrice = in.readDouble();
        this.instrumentId = in.readString();
        this.lastPrice = in.readDouble();
        this.lowestPrice = in.readDouble();
        this.openPrice = in.readDouble();
        this.preClsPrice = in.readDouble();
        this.preSetPrice = in.readDouble();
        this.settlePrice = in.readDouble();
        this.tradeDay = in.readString();
        this.upDropPrice = in.readDouble();
        this.upDropSpeed = in.readDouble();
        this.upTime = in.readLong();
        this.positionVolume = in.readLong();
        this.prePositionVolume = in.readLong();
        this.downLimitPrice = in.readDouble();
        this.upLimitPrice = in.readDouble();
        this.exchangeId = in.readString();
        this.openInterest = in.readLong();
        this.preOpenInterest = in.readLong();
        this.turnover = in.readDouble();
        this.upTimeFormat = in.readString();
        this.volume = in.readLong();
    }

    public static final Creator<FullMarketData> CREATOR = new Creator<FullMarketData>() {
        @Override
        public FullMarketData createFromParcel(Parcel source) {
            return new FullMarketData(source);
        }

        @Override
        public FullMarketData[] newArray(int size) {
            return new FullMarketData[size];
        }
    };
}
