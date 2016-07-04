package com.jnhyxx.html5;

public class Variant {
    public static final String FLAVOR_APP1 = "app1";
    public static final String FLAVOR_ORIGIN = "origin";
    public static final String FLAVOR_H5 = "forH5";
    public static final String FLAVOR_MICROIL = "microil";
    public static final String FLAVOR_YCP = "ycp";

    public static boolean isApp1() {
        return BuildConfig.FLAVOR.equals(FLAVOR_APP1);
    }

    public static boolean isOrigin() {
        return BuildConfig.FLAVOR.equals(FLAVOR_ORIGIN);
    }

    public static boolean isForH5() {
        return BuildConfig.FLAVOR.equals(FLAVOR_H5);
    }

    public static boolean isMicroil() {
        return BuildConfig.FLAVOR.equals(FLAVOR_MICROIL);
    }

    public static boolean isYcp() {
        return BuildConfig.FLAVOR.equals(FLAVOR_YCP);
    }

}

