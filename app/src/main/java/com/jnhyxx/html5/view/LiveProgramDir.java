package com.jnhyxx.html5.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;

import com.jnhyxx.html5.R;
import com.jnhyxx.html5.domain.live.LiveMessage;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

/**
 * Created by ${wangJie} on 2016/11/9.
 * 节目单的popupWindow
 */

public class LiveProgramDir {

    private static Context sContext;
    private static WeakReference<Context> sWeakReference;

    private static PopupWindow sPopupWindow;

    public static void showLiveProgramDirPopupWindow(Context context, ArrayList<LiveMessage.ProgramInfo> programInfoArrayList) {
        sWeakReference = new WeakReference<Context>(context);
        View popupView = LayoutInflater.from(sWeakReference.get()).inflate(R.layout.popup_window_live_program, null);
        ListView listView = (ListView) popupView.findViewById(R.id.listView);
        ArrayAdapter<LiveMessage.ProgramInfo> programInfoArrayAdapter = new ArrayAdapter<LiveMessage.ProgramInfo>(sWeakReference.get());

        initPopupWindow(popupView);
    }

    private static void initPopupWindow(View popupView) {
        sPopupWindow = new PopupWindow(popupView, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        sPopupWindow.setOutsideTouchable(true);
        sPopupWindow.setFocusable(true);
        sPopupWindow.setBackgroundDrawable(new BitmapDrawable(sWeakReference.get().getResources(), (Bitmap) null));
        sPopupWindow.setClippingEnabled(true);
    }
}
