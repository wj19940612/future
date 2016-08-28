package com.johnz.kutils.net;

import android.text.TextUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;

public class CookieManger {

    private static String FILE_NAME = "cookie_cache";

    private static CookieManger sParser;

    private File mAppDataDir;
    private String mRawCookie;

    public static CookieManger getInstance() {
        if (sParser == null) {
            sParser = new CookieManger();
        }
        return sParser;
    }

    public void init(File file) {
        mAppDataDir = file;
    }

    /**
     * token1="NzF4aGpldmJhcHRmd3NleHZucWJudm1ocWU4NQ=="; Version=1; Path=/
     * token2="NzQ5ZjAxMjE0ZjQzZWE4ZjI3NGIyYzkyNTIzYmY0MWQ="; Version=1; Path=/
     */
    public void parse(Map<String, String> headers) {
        String rawCookie = headers.get("Set-Cookie");
        if (!TextUtils.isEmpty(rawCookie)) {

            if (!rawCookie.equals(mRawCookie)) {
                mRawCookie = rawCookie;
                saveRawCookies();
            }
        }
    }

    public String getCookies() {
        StringBuilder builder = new StringBuilder();

        if (TextUtils.isEmpty(mRawCookie)) {
            mRawCookie = getRawCookie();
        }

        if (!TextUtils.isEmpty(mRawCookie)) {
            String[] cookies = mRawCookie.split("\n");
            for (int i = 0; i < cookies.length; i++) {
                builder.append(getToken(cookies[i])).append("; ");
            }
            if (builder.length() > 0) {
                builder.delete(builder.length() - 2, builder.length() - 1);
            }
        }
        return builder.toString();
    }

    private void saveRawCookies() {
        File file = new File(mAppDataDir, FILE_NAME);
        FileOutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(file);
            outputStream.write(mRawCookie.getBytes());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private String getRawCookie() {
        String result = "";
        File file = new File(mAppDataDir, FILE_NAME);
        if (!file.exists()) return result;

        FileInputStream inputStream = null;
        try {
            inputStream = new FileInputStream(file);
            byte[] bytes = new byte[inputStream.available()];
            while (inputStream.read(bytes) != -1) {
                result = new String(bytes);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
            return result;
        }
    }

    private String getToken(String cookie) {
        String[] splits = cookie.split(";");
        for (String split : splits) {
            if (split.indexOf("token") != -1) {
                return split;
            }
        }
        return "";
    }

    private String getTokenContent(String tokenStr) {
        int firstQuotationMark = tokenStr.indexOf("\"");
        int secondQuotationMark = tokenStr.indexOf("\"", firstQuotationMark + 1);
        return tokenStr.substring(firstQuotationMark + 1, secondQuotationMark);
    }
}
