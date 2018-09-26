package com.jnhyxx.html5.domain.market;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.jnhyxx.html5.Preference;
import com.jnhyxx.html5.net.API;
import com.jnhyxx.html5.net.Callback2;
import com.jnhyxx.html5.net.Resp;

import java.util.List;

public class ServerIpPort implements Parcelable {

    /**
     * port : 8068
     * ip : 139.129.221.2
     */

    private String port;
    private String ip;

    public String getPort() {
        if (TextUtils.isEmpty(port)) {
            port = "0";
        }
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

    public boolean isValid() {
        return !TextUtils.isEmpty(ip) && !TextUtils.isEmpty(port);
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

    public static void saveMarketServerIpPort(ServerIpPort ipPort) {
        String ipPortStr = new Gson().toJson(ipPort);
        Preference.get().setMarketServerIpPort(ipPortStr);
    }

    public static ServerIpPort getMarketServerIpPort() {
        String ipPortStr = Preference.get().getMarketServerIpPort();
        if (!TextUtils.isEmpty(ipPortStr)) {
            return new Gson().fromJson(ipPortStr, ServerIpPort.class);
        }
        return null;
    }

    public static void requestMarketServerIpAndPort(@Nullable final Callback callback) {
        API.Market.getMarketServerIpAndPort()
                .setCallback(new Callback2<Resp<List<ServerIpPort>>, List<ServerIpPort>>() {
                    @Override
                    public void onRespSuccess(List<ServerIpPort> serverIpPorts) {
                        if (serverIpPorts.size() > 0) {
                            saveMarketServerIpPort(serverIpPorts.get(0));
                            if (callback != null) {
                                callback.onSuccess(serverIpPorts.get(0));
                            }
                        }
                    }
                }).fireSync();
    }

    public interface Callback {
        void onSuccess(ServerIpPort ipPort);
    }

    @Override
    public String toString() {
        return "ServerIpPort{" +
                "port='" + port + '\'' +
                ", ip='" + ip + '\'' +
                '}';
    }
}
