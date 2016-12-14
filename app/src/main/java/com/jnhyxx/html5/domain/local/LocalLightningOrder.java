package com.jnhyxx.html5.domain.local;

import android.text.TextUtils;

import com.jnhyxx.html5.Preference;
import com.jnhyxx.html5.domain.order.ProductLightningOrderStatus;

/**
 * Created by ${wangJie} on 2016/12/14.
 * 管理本地的闪现下单model
 */

public class LocalLightningOrder {

    private static LocalLightningOrder sLocalLightningOrder;

    private LocalLightningOrder() {

    }

    public static LocalLightningOrder getLocalLightningOrder() {
        if (sLocalLightningOrder != null) {
            return sLocalLightningOrder;
        }
        return new LocalLightningOrder();
    }


    //本地存储闪电下单数据
    public void setLightningOrder(String lightningOrderKey, ProductLightningOrderStatus productLightningOrderStatus) {
        Preference.get().setLightningOrderStatus(lightningOrderKey, productLightningOrderStatus);
    }

    //获取本地的闪电数据
    public static ProductLightningOrderStatus getLocalLightningOrderStatus(String lightningOrderKey) {
        return Preference.get().getLightningOrderStatus(lightningOrderKey);
    }

    //判断产品闪电下单是否开启
    public static boolean isLightningOrderOpen(String lightningOrderKey) {
        return !TextUtils.isEmpty(lightningOrderKey) && getLocalLightningOrderStatus(lightningOrderKey) != null;
    }
}
