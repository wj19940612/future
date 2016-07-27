package com.jnhyxx.html5.view;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.jnhyxx.html5.R;

import java.util.concurrent.atomic.AtomicInteger;

public class Progress {

    private AtomicInteger mCounter;
    private Dialog mDialog;
    private DialogInterface.OnCancelListener mOnCancelListener;

    public Progress(DialogInterface.OnCancelListener cancelListener) {
        mCounter = new AtomicInteger(0);
        mOnCancelListener = cancelListener;
    }

    public void show(Context context) {
        if (mCounter.get() == 0) {
            if (mDialog == null) {
                mDialog = ProgressDialog.show(context, mOnCancelListener);
            } else {
                mDialog.show();
            }
            mCounter.incrementAndGet();
        } else {
            mCounter.incrementAndGet();
        }
        Log.d("TEST", "show: " + mCounter.get());
    }

    public void dismiss() {
        if (mCounter.get() == 0) {
            return;
        } else {
            mCounter.decrementAndGet();
        }

        Log.d("TEST", "dismiss: " + mCounter.get());

        if (mCounter.get() == 0 && mDialog != null && mDialog.isShowing()) {
            Log.d("TEST", "close: ");
            mDialog.dismiss();
        }
    }

    public void dismissAll() {
        mCounter.set(0);
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
        }
    }

    private static class ProgressDialog {

        public static Dialog show(Context context, DialogInterface.OnCancelListener cancelListener) {
            Dialog dialog = new Dialog(context, R.style.DialogTheme_NoTitle);
            dialog.setCancelable(true);
            dialog.setCanceledOnTouchOutside(false);
            dialog.setContentView(createView(context));
            dialog.setOnCancelListener(cancelListener);
            dialog.show();
            return dialog;
        }

        private static View createView(Context context) {
            RelativeLayout rootView = new RelativeLayout(context);
            ProgressBar progressBar = new ProgressBar(context, null, android.R.attr.progressBarStyle);
            progressBar.setIndeterminate(true);
            progressBar.setVisibility(View.VISIBLE);
            RelativeLayout.LayoutParams params = new RelativeLayout
                    .LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.addRule(RelativeLayout.CENTER_IN_PARENT);
            rootView.addView(progressBar, params);
            return rootView;
        }
    }
}
