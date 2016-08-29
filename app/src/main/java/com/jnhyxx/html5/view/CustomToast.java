package com.jnhyxx.html5.view;

import android.content.Context;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.jnhyxx.html5.R;
import com.jnhyxx.html5.utils.CommonMethodUtils;

/**
 * Created by Administrator on 2016/8/29.
 * 带图片的toast
 */


public class CustomToast {
    Toast mToast;

    private static class Instance {
        static CustomToast customToast = new CustomToast();
    }

    public static CustomToast getInstance() {
        return Instance.customToast;
    }

    private CustomToast() {

    }

    public void makeText(Context context, int stringId) {
        this.makeText(context, context.getString(stringId));
    }

    public void makeText(Context context, String tiastText) {
        if (mToast == null) {
            mToast = Toast.makeText(context, tiastText, Toast.LENGTH_SHORT);
        }
        mToast.setGravity(Gravity.CENTER, 0, 0);
        LinearLayout view = (LinearLayout) mToast.getView();
        view.setGravity(Gravity.CENTER);
        ImageView imageView = new ImageView(context);
//        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setImageResource(R.drawable.account_trade_warn_list_icon_succeed);
        int screenWidth = CommonMethodUtils.getScreenWidth(context);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(screenWidth / 2, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(50, 50, 50, 50);
        view.addView(imageView, 0, params);
        mToast.show();
    }

}
