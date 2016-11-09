package com.lecloud.skin.ui.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

@SuppressWarnings("all")
public class WaterMarkImageView extends ImageView {

    public WaterMarkImageView(Context context, String url) {
        super(context);
        loadImage(url);
    }
    public void setWaterMarkUrl( String url){
        loadImage(url);
    }
    public WaterMarkImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    private void loadImage(String url) {
        new AsyncTask<String, Integer, Bitmap>() {
            @Override
            protected Bitmap doInBackground(String... params) {
                if (params == null || params.length == 0) {
                    return null;
                }
                return getHttpBitmap(params[0]);
            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                super.onPostExecute(bitmap);
                if (bitmap != null) {
                    setImageBitmap(bitmap);
                }
            }
        }.execute(url);
    }

    private static Bitmap getHttpBitmap(String url) {
        URL myBitmapUrl = null;
        Bitmap bitmap = null;
        try {
            myBitmapUrl = new URL(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        try {
            HttpURLConnection conn = (HttpURLConnection) myBitmapUrl.openConnection();
            conn.setConnectTimeout(0);
            conn.setDoInput(true);
            conn.connect();
            InputStream is = conn.getInputStream();
            bitmap = BitmapFactory.decodeStream(is);
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

}
