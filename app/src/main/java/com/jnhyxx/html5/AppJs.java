package com.jnhyxx.html5;

import android.content.Context;
import android.content.Intent;
import android.webkit.JavascriptInterface;

import com.jnhyxx.html5.activity.MainActivity1;

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

    /**
     * 用于统计的前端调用注册API
     */
    @JavascriptInterface
    public void onSTTRegister(String... args) {
        if (context != null && context instanceof MainActivity1) {
            MainActivity1 activity = (MainActivity1) context;
            activity.getJSMain().on_Cmd_Register(args);
        }
    }

    /**
     * 用于统计的前端调用买入API
     */
    @JavascriptInterface
    public void onSTTBuy(String... args) {
        if (context != null && context instanceof MainActivity1) {
            MainActivity1 activity = (MainActivity1) context;
            activity.getJSMain().on_Cmd_Buy(args);
        }
    }

    /**
     * 用于统计的前端调用卖出API
     */
    @JavascriptInterface
    public void onSTTSell(String... args) {
        if (context != null && context instanceof MainActivity1) {
            MainActivity1 activity = (MainActivity1) context;
            activity.getJSMain().on_Cmd_Sell(args);
        }
    }
}
