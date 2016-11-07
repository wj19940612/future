package com.lecloud.skin.ui.view;

import com.lecloud.skin.R;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;


/**
 * Created by gaolinhua on 2016/5/24.
 */
public class VideoLoading extends RelativeLayout {
    private ProgressBar mProgressBar;
    private RelativeLayout mStartLoading;
    private WaterMarkImageView picWaterMark;
    private ImageView lineLoading;
    public VideoLoading(Context context) {
        super(context);
        LayoutInflater.from(getContext()).inflate(R.layout.letv_skin_v4_video_loading, this, true);
        mProgressBar = (ProgressBar) findViewById(R.id.progress_loading);
        mStartLoading = (RelativeLayout) findViewById(R.id.video_start_loading);
        lineLoading = (ImageView) findViewById(R.id.line_loading);
        picWaterMark = (WaterMarkImageView) findViewById(R.id.image_loading);
    }

    public void  setLoadingUrl(String url){
        init(url);
    }

    private void init(String url) {
        picWaterMark.setWaterMarkUrl(url);
        ((AnimationDrawable) lineLoading.getDrawable()).start();
    }

    public void showLoadingAnimation() {
        setVisibility(View.VISIBLE);
        mStartLoading.setVisibility(View.VISIBLE);
        mProgressBar.setVisibility(View.GONE);
    }

    public void showLoadingProgress() {
        setVisibility(View.VISIBLE);
        mProgressBar.setVisibility(View.VISIBLE);
        mStartLoading.setVisibility(View.GONE);
    }

    public void hide(){
        setVisibility(View.GONE);
    }
}
