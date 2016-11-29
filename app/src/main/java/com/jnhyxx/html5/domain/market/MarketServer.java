package com.jnhyxx.html5.domain.market;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class MarketServer implements Parcelable,Serializable {

    private static final long serialVersionUID = 6042135448453948204L;

    public static final String EX_MARKET_SERVER = "marketServer";

    /**
     * port : 8068
     * ip : 139.129.221.2
     */

    private String port;
    private String ip;

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.port);
        dest.writeString(this.ip);
    }

    public MarketServer() {
    }

    protected MarketServer(Parcel in) {
        this.port = in.readString();
        this.ip = in.readString();
    }

    public static final Parcelable.Creator<MarketServer> CREATOR = new Parcelable.Creator<MarketServer>() {
        @Override
        public MarketServer createFromParcel(Parcel source) {
            return new MarketServer(source);
        }

        @Override
        public MarketServer[] newArray(int size) {
            return new MarketServer[size];
        }
    };
}
