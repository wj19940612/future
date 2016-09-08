package com.jnhyxx.html5.view;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
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

    public void makeText(Context context, String toastText) {
        if (mToast == null) {
            mToast = Toast.makeText(context, toastText, Toast.LENGTH_SHORT);
        }
        mToast.setGravity(Gravity.CENTER, 0, 0);
        LinearLayout view = (LinearLayout) mToast.getView();
        view.setGravity(Gravity.CENTER);
        ImageView imageView = new ImageView(context);
//        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setImageResource(R.drawable.ic_common_toast_succeed);
        int screenWidth = CommonMethodUtils.getScreenWidth(context);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(screenWidth / 2, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(50, 50, 50, 50);
        view.addView(imageView, 0, params);
        mToast.show();
    }

    /**
     * 完全自定义的Toast
     */
    public void custommakeText(Context context, String toastText) {
        if (mToast == null) {
            mToast = new Toast(context);
        }
        View mView = LayoutInflater.from(context).inflate(R.layout.common_custom_toast, null);
        TextView tvToastText = (TextView) mView.findViewById(R.id.commonCustomToastTxt);
        tvToastText.setText(toastText);
        mToast.setGravity(Gravity.CENTER, 0, 0);
        mToast.setView(mView);
        mToast.show();
    }

    public void custommakeText(Context context, int toastText) {
        custommakeText(context, context.getString(toastText));
    }
}
