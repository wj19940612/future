package com.johnz.kutils.net;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CookieManger {

    private static CookieManger sParser;

    private List<String> mTokenKeys;

    public static CookieManger getInstance() {
        if (sParser == null) {
            sParser = new CookieManger();
        }
        return sParser;
    }

    public void init(Context context, String[] tokenKeys) {
        mTokenKeys = new ArrayList<>();
        for (String tokenKey : tokenKeys) {
            mTokenKeys.add(tokenKey);
        }
    }

    /**
     * token1="NzF4aGpldmJhcHRmd3NleHZucWJudm1ocWU4NQ=="; Version=1; Path=/
     * token2="NzQ5ZjAxMjE0ZjQzZWE4ZjI3NGIyYzkyNTIzYmY0MWQ="; Version=1; Path=/
     */
    public String parse(Map<String, String> headers) {
        String rawCookie = headers.get("Set-Cookie");
        if (!TextUtils.isEmpty(rawCookie)) {
            int cursor = 0;
            for (String key : mTokenKeys) {
                cursor = rawCookie.indexOf(key, cursor);
                if (cursor != -1) {
                    String tokenStr = getSubTokenStr(cursor, rawCookie);
                    String cookie = getTokenContent(tokenStr);
                    Log.d("TEST", "parse: " + cookie);
                }
            }
        }
        return "";
    }

    private String getSubTokenStr(int start, String rawCookie) {
        return rawCookie.substring(start, rawCookie.indexOf(";", start));
    }

    private String getTokenContent(String tokenStr) {
        int firstQuotationMark = tokenStr.indexOf("\"");
        int secondQuotationMark = tokenStr.indexOf(tokenStr, firstQuotationMark + 1);
        return tokenStr.substring(firstQuotationMark, secondQuotationMark);
    }
}
