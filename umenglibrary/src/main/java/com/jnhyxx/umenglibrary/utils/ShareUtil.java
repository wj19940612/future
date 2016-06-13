package com.jnhyxx.umenglibrary.utils;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.widget.Toast;

import com.jnhyxx.umenglibrary.R;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.shareboard.SnsPlatform;
import com.umeng.socialize.utils.ShareBoardlistener;

public class ShareUtil {

    private static ShareUtil sInstance;

    private String mTitle;
    private String mContent;
    private String mTargetUrl;
    private int mShareIcon;

    public static ShareUtil getInstance() {
        if (sInstance == null) {
            sInstance = new ShareUtil();
        }
        return sInstance;
    }

    public static class ShareResultListener implements UMShareListener {

        private Context mContext;

        public ShareResultListener(Context context) {
            mContext = context;
        }

        @Override
        public void onResult(SHARE_MEDIA share_media) {
            Toast.makeText(mContext, R.string.share_success, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onError(SHARE_MEDIA share_media, Throwable throwable) {
            Toast.makeText(mContext, R.string.share_failure, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCancel(SHARE_MEDIA share_media) {
            Toast.makeText(mContext, R.string.share_canceled, Toast.LENGTH_SHORT).show();
        }
    }

    public void setShare(String title, String content, String targetUrl, int shareIcon) {
        this.mTitle = title;
        this.mContent = content;
        this.mTargetUrl = targetUrl;
        this.mShareIcon = shareIcon;
    }

    public void openShareBoard(final Activity activity, final ShareResultListener listener) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {

                new ShareAction(activity)
                        .setDisplayList(SHARE_MEDIA.WEIXIN,
                                SHARE_MEDIA.WEIXIN_CIRCLE,
                                SHARE_MEDIA.QZONE,
                                SHARE_MEDIA.SINA,
                                SHARE_MEDIA.QQ)
                        .setShareboardclickCallback(new ShareBoardlistener() {
                            @Override
                            public void onclick(SnsPlatform snsPlatform, SHARE_MEDIA share_media) {
                                ShareAction action = new ShareAction(activity)
                                        .withMedia(new UMImage(activity, mShareIcon))
                                        .setPlatform(share_media).setCallback(listener);

                                if (!TextUtils.isEmpty(mTitle)) {
                                    action.withTitle(mTitle);
                                }

                                if (!TextUtils.isEmpty(mContent)) {
                                    action.withText(mContent);
                                }

                                if (!TextUtils.isEmpty(mTargetUrl)) {
                                    action.withTargetUrl(mTargetUrl);
                                }

                                action.share();
                            }
                        })
                        .open();
            }
        });
    }
}
