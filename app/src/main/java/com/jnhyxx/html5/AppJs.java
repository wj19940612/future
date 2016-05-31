package com.jnhyxx.html5;

import android.content.Context;
import android.content.Intent;
import android.webkit.JavascriptInterface;

import java.net.URISyntaxException;

public class AppJs {

    private Context context;

    public AppJs(Context context) {
        this.context = context;
    }

    /**
     * 打开app客户端
     */
    @JavascriptInterface
    public void openAppByUrl(String url) {
        if (context != null) {
            try {
                Intent intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME);
                if (intent.resolveActivity(context.getPackageManager()) != null) {
                    context.startActivity(intent);
                } else {
                    //ToastUtil.showShortToastMsg(R.string.please_install_qq_first);
                }
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }
    }
}
