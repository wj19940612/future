package com.jnhyxx.html5.utils;


import android.util.ArrayMap;
import android.util.Log;

import com.jnhyxx.html5.domain.account.UserInfo;
import com.jnhyxx.html5.domain.local.LocalUser;
import com.jnhyxx.html5.domain.market.ProductLightningOrderStatus;

/**
 * Created by ${wangJie} on 2016/11/29.
 */

public class LightningOrdersArrayMap {
    private static final String TAG = "LightningOrdersArrayMap";

    static ArrayMap<String, ProductLightningOrderStatus> data = new ArrayMap<>();


    private LightningOrdersArrayMap() {
    }

    private static class Instance {
        static LightningOrdersArrayMap sLightningOrdersArrayMap = new LightningOrdersArrayMap();
    }

    public static LightningOrdersArrayMap getInstance() {
        return Instance.sLightningOrdersArrayMap;
    }

    public void setLightningOrders(ProductLightningOrderStatus productLightningOrderStatus) {
        UserInfo userInfo = LocalUser.getUser().getUserInfo();
        if (userInfo != null) {
            String key = userInfo.getUserPhone() + productLightningOrderStatus.getVarietyId() + productLightningOrderStatus.getPayType();
            Log.d(TAG, "存入的key  " + key + " 闪电下单数据 " + productLightningOrderStatus);
            data.put(key, productLightningOrderStatus);
        }
    }

    public ProductLightningOrderStatus getLocalLightningStatus(int varietyId, int psyType) {
        UserInfo userInfo = LocalUser.getUser().getUserInfo();
        if (userInfo != null) {
            String key = userInfo.getUserPhone() + varietyId + varietyId;
            if (data.containsKey(key)) {
                return data.get(key);
            }
        }
        return null;
    }

    public void clear() {
        data.clear();
    }
}
