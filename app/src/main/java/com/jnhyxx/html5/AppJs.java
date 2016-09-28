package com.jnhyxx.html5;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.webkit.JavascriptInterface;

import com.jnhyxx.html5.activity.MainActivity;
import com.jnhyxx.umenglibrary.utils.ShareUtil;

import java.net.URISyntaxException;

public class AppJs {

    private Context mContext;

    public AppJs(Context context) {
        mContext = context;
    }

    /**
     * 打开app客户端
     */
    @JavascriptInterface
    public void openAppByUrl(String url) {
        if (mContext != null) {
            try {
                Intent intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME);
                if (intent.resolveActivity(mContext.getPackageManager()) != null) {
                    mContext.startActivity(intent);
                } else {
                    //ToastUtil.showShortToastMsg(R.string.please_install_qq_first);
                }
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }
    }

    @JavascriptInterface
    public void openShareBoard(String title, String content, String url) {
        if (mContext != null && mContext instanceof MainActivity) {
            ShareUtil.getInstance().setShare(title, content, url, R.mipmap.ic_launcher);
            MainActivity activity = (MainActivity) mContext;
            ShareUtil.getInstance().openShareBoard(activity, new ShareUtil.ShareResultListener(mContext));
        }
    }

    @JavascriptInterface
    public boolean copyToClipboard(String copiedText) {
        ClipboardManager clipboardManager = (ClipboardManager)
                App.getAppContext().getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clipData = ClipData.newPlainText(copiedText, copiedText);
        clipboardManager.setPrimaryClip(clipData);
        return clipboardManager.hasPrimaryClip();
    }

    @JavascriptInterface
    public void updateUmengDeviceId(String token) {
        if (Variant.isOrigin() || Variant.isTest()) {
            
        }
    }
}
