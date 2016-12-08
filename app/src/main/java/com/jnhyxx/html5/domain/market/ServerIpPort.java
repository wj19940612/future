package com.jnhyxx.html5.domain.market;

import android.os.Parcel;
import android.os.Parcelable;



public class ServerIpPort implements Parcelable{

    private static final long serialVersionUID = -6174707126558118139L;

    public static final String EX_IP_PORT = "ip_port";

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

    public ServerIpPort() {
    }

    protected ServerIpPort(Parcel in) {
        this.port = in.readString();
        this.ip = in.readString();
    }

    public static final Parcelable.Creator<ServerIpPort> CREATOR = new Parcelable.Creator<ServerIpPort>() {
        @Override
        public ServerIpPort createFromParcel(Parcel source) {
            return new ServerIpPort(source);
        }

        @Override
        public ServerIpPort[] newArray(int size) {
            return new ServerIpPort[size];
        }
    };
}
