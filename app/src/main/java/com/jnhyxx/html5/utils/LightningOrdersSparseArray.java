package com.jnhyxx.html5.utils;

import android.util.SparseArray;

/**
 * Created by ${wangJie} on 2016/11/29.
 */

public class LightningOrdersSparseArray<ProductLightningOrderStatus> extends SparseArray<ProductLightningOrderStatus> {

    private LightningOrdersSparseArray() {
    }

    private static class Instance {
        static LightningOrdersSparseArray sLightningOrdersSparseArray = new LightningOrdersSparseArray();
    }

    public static LightningOrdersSparseArray getInstance() {
        return Instance.sLightningOrdersSparseArray;
    }

    @Override
    public ProductLightningOrderStatus get(int key, ProductLightningOrderStatus valueIfKeyNotFound) {
        return super.get(key, valueIfKeyNotFound);
    }
}
