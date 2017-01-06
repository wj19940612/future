package com.jnhyxx.html5.fragment.dialog;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;

import com.jnhyxx.html5.R;

/**
 * Created by ${wangJie} on 2017/1/6.
 * 居于底部的dialogFragment
 */

public class BaseGravityBottomDialogFragment extends DialogFragment {
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NO_TITLE, R.style.AlertDialogStyle);
    }

    public void show(FragmentManager manager) {
        this.show(manager, UploadUserImageDialogFragment.class.getSimpleName());
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Window window = getDialog().getWindow();
        if (window != null) {
            window.setGravity(Gravity.BOTTOM);
            DisplayMetrics dm = new DisplayMetrics();
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
            window.setLayout(dm.widthPixels, WindowManager.LayoutParams.WRAP_CONTENT);
        }
    }
}
