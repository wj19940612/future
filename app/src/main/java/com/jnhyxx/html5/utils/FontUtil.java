package com.jnhyxx.html5.utils;

import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jnhyxx.html5.App;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class FontUtil {

    private static Typeface getOpenSansRegularFont() {
        return Typeface.createFromAsset(App.getAppContext().getAssets(),
                "fonts/OpenSans-Regular.ttf");
    }

    private static Typeface getOpenSansBoldFont() {
        return Typeface.createFromAsset(App.getAppContext().getAssets(),
                "fonts/OpenSans-Bold.ttf");
    }

    private static Typeface getTt0173MFont() {
        return Typeface.createFromAsset(App.getAppContext().getAssets(),
                "fonts/tt0173m.ttf");
    }

    public static void setTt0173MFont(View view) {
        if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                setTt0173MFont(viewGroup.getChildAt(i));
            }
        } else if (view instanceof TextView) {
            TextView textView = (TextView) view;
            textView.setTypeface(getTt0173MFont());
        }
    }



    public static   String getAssetsCacheFile(Context context, String fileName) {
        File cacheFile = new File(context.getCacheDir(), fileName);
        try {
            InputStream inputStream = context.getAssets().open(fileName);
            try {
                FileOutputStream outputStream = new FileOutputStream(cacheFile);
                try {
                    byte[] buf = new byte[1024];
                    int len;
                    while ((len = inputStream.read(buf)) > 0) {
                        outputStream.write(buf, 0, len);
                    }
                } finally {
                    outputStream.close();
                }
            } finally {
                inputStream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return cacheFile.getAbsolutePath();
    }

//    public static void setOpenSans(View view) {
//        if (view instanceof ViewGroup) {
//            ViewGroup viewGroup = (ViewGroup) view;
//            for (int i = 0; i < viewGroup.getChildCount(); i++) {
//                setOpenSans(viewGroup.getChildAt(i));
//            }
//        } else if (view instanceof TextView) {
//            TextView textView = (TextView) view;
//            Typeface typeface = textView.getTypeface();
//            if (typeface != null && typeface.isBold()) {
//                textView.setTypeface(getOpenSansBoldFont());
//            } else {
//                textView.setTypeface(getOpenSansRegularFont());
//            }
//        }
//    }
}
