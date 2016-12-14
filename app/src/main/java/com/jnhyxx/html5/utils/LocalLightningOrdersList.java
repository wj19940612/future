package com.jnhyxx.html5.utils;


import android.util.Log;

import com.jnhyxx.html5.domain.account.UserInfo;
import com.jnhyxx.html5.domain.local.LocalUser;
import com.jnhyxx.html5.domain.order.ProductLightningOrderStatus;

import java.util.HashMap;

/**
 * Created by ${wangJie} on 2016/11/29.
 */

public class LocalLightningOrdersList {
    private static final String TAG = "LocalLightningOrders";

    private static HashMap<String, ProductLightningOrderStatus> data = new HashMap<>();

    private LocalLightningOrdersList() {
    }

    private static class Instance {
        static LocalLightningOrdersList sLocalLightningOrdersList = new LocalLightningOrdersList();
    }

    public static LocalLightningOrdersList getInstance() {
        return Instance.sLocalLightningOrdersList;
    }

    public void setLightningOrders(ProductLightningOrderStatus productLightningOrderStatus) {
        UserInfo userInfo = LocalUser.getUser().getUserInfo();
        if (userInfo != null) {
            String key = userInfo.getUserPhone() + productLightningOrderStatus.getVarietyId() + productLightningOrderStatus.getPayType();
            Log.d(TAG, "存入的key  " + key + " 闪电下单数据 " + productLightningOrderStatus.toString());
            data.put(key, productLightningOrderStatus);
        }
    }

    public ProductLightningOrderStatus getLocalLightningStatus(int varietyId, int psyType) {
        UserInfo userInfo = LocalUser.getUser().getUserInfo();
        if (userInfo != null) {
            String key = userInfo.getUserPhone() + varietyId + psyType;
            if (data.containsKey(key)) {
                return data.get(key);
            }
        }
        return null;
    }

    public boolean clearLightningOrder(int varietyId, int psyType) {
        UserInfo userInfo = LocalUser.getUser().getUserInfo();
        if (userInfo != null) {
            String key = userInfo.getUserPhone() + varietyId + psyType;
            if (data.containsKey(key)) {
                data.remove(key);
            }
        }
        return false;
    }

    public void clear() {
        data.clear();
    }
}
