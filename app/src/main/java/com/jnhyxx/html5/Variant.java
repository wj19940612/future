package com.jnhyxx.html5;

public class Variant {
    public static final String FLAVOR_ORIGIN = "origin";
    public static final String FLAVOR_TEST = "tst";
    public static final String FLAVOR_MICROIL = "microil";
    public static final String FLAVOR_YCP = "ycp";


    public static boolean isOrigin() {
        return BuildConfig.FLAVOR.equals(FLAVOR_ORIGIN);
    }

    public static boolean isTest() {
        return BuildConfig.FLAVOR.equals(FLAVOR_TEST);
    }

    public static boolean isMicroil() {
        return BuildConfig.FLAVOR.equals(FLAVOR_MICROIL);
    }

    public static boolean isYcp() {
        return BuildConfig.FLAVOR.equals(FLAVOR_YCP);
    }

}

