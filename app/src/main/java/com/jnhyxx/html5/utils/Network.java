package com.jnhyxx.html5.utils;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;

import com.jnhyxx.html5.App;

public class Network {

    public static boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) App.getAppContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        return networkInfo != null && networkInfo.isConnectedOrConnecting();
    }

    public static void registerNetworkChangeReceiver(Activity activity, BroadcastReceiver receiver) {
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        if (activity != null) {
            activity.registerReceiver(receiver, filter);
        }
    }

    public static void unregisterNetworkChangeReceiver(Activity activity, BroadcastReceiver receiver) {
        if (activity != null) {
            try {
                activity.unregisterReceiver(receiver);
            } catch (IllegalArgumentException e) { // throw when receiver not register
                e.printStackTrace();
            }
        }
    }

    public static abstract class NetworkChangeReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equalsIgnoreCase(ConnectivityManager.CONNECTIVITY_ACTION)) {
                int availableNetworkType = Network.getAvailableNetwork();
                onNetworkChanged(availableNetworkType);
            }
        }

        protected abstract void onNetworkChanged(int availableNetworkType);
    }

    public final static int NET_NONE = 0;
    public final static int NET_WIFI = 1;
    public final static int NET_2G = 2;
    public final static int NET_3G = 3;
    public final static int NET_4G = 4;

    private static int getAvailableNetwork() {
        int availableNetwork = NET_NONE;

        ConnectivityManager connectivityManager = (ConnectivityManager) App.getAppContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo == null) {
            return availableNetwork;
        }

        if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
            availableNetwork = NET_WIFI;
        } else if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
            int subType = networkInfo.getSubtype();

            if (subType == TelephonyManager.NETWORK_TYPE_CDMA
                    || subType == TelephonyManager.NETWORK_TYPE_GPRS
                    || subType == TelephonyManager.NETWORK_TYPE_EDGE) {
                availableNetwork = NET_2G;

            } else if (subType == TelephonyManager.NETWORK_TYPE_UMTS
                    || subType == TelephonyManager.NETWORK_TYPE_HSDPA
                    || subType == TelephonyManager.NETWORK_TYPE_EVDO_A
                    || subType == TelephonyManager.NETWORK_TYPE_EVDO_0
                    || subType == TelephonyManager.NETWORK_TYPE_EVDO_B) {
                availableNetwork = NET_3G;
            } else if (subType == TelephonyManager.NETWORK_TYPE_LTE) {
                availableNetwork = NET_4G;
            }
        }

        return availableNetwork;
    }
}
