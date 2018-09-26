package com.jnhyxx.html5;

public class Variant {
    public static final String FLAVOR_ORIGIN = "origin";
    public static final String FLAVOR_TEST = "tst";

    public static boolean isOrigin() {
        return BuildConfig.FLAVOR.equals(FLAVOR_ORIGIN);
    }

    public static boolean isTest() {
        return BuildConfig.FLAVOR.equals(FLAVOR_TEST);
    }
}

