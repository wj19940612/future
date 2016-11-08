package com.jnhyxx.html5.activity;

import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.jnhyxx.html5.R;
import com.jnhyxx.html5.fragment.live.VideoPlayFragment;
import com.jnhyxx.html5.view.SlidingTabLayout;
import com.jnhyxx.html5.view.TitleBar;
import com.lecloud.sdk.constant.PlayerParams;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LiveActivity extends BaseActivity {

    @BindView(R.id.liveLayout)
    RelativeLayout mLiveLayout;
    @BindView(R.id.slidingTabLayout)
    SlidingTabLayout mSlidingTabLayout;
    @BindView(R.id.viewPager)
    ViewPager mViewPager;
    @BindView(R.id.activity_live)
    LinearLayout mActivityLive;
    @BindView(R.id.titleBar)
    TitleBar mTitleBar;

    // TODO: 2016/11/8 房间Id 
//    private String mLiveId = "A2016080200000n1";
    private String mLiveId = "A2016053100000je";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setFormat(PixelFormat.TRANSLUCENT);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live);
        ButterKnife.bind(this);

        initVideoPlayFragment();
    }

    private void initVideoPlayFragment() {
        Bundle bundle = setBundle();
        FragmentManager mSupportFragmentManager = getSupportFragmentManager();
        if (mSupportFragmentManager.findFragmentByTag(VideoPlayFragment.class.getSimpleName()) == null) {
            VideoPlayFragment videoPlayFragment = VideoPlayFragment.newInstance(bundle);
            videoPlayFragment.setOnConfigurationChangedListener(new VideoPlayFragment.OnConfigurationChangedListener() {
                @Override
                public void onConfigurationChanged(Configuration newConfig) {
                    if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                        Log.d(TAG, "横屏");
                        hideLayout();
                    } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
                        Log.d(TAG, "竖屏");
                        showLayout();
                    }
                }
            });
            FragmentTransaction mFragmentTransaction = mSupportFragmentManager.beginTransaction();
            mFragmentTransaction.replace(R.id.liveLayout, videoPlayFragment, VideoPlayFragment.class.getSimpleName()).commitAllowingStateLoss();
        }
    }

    private void showLayout() {
        mTitleBar.setVisibility(View.VISIBLE);
    }

    private void hideLayout() {
        mTitleBar.setVisibility(View.GONE);
    }

    private Bundle setBundle() {
        Bundle mBundle = new Bundle();
        mBundle.putInt(PlayerParams.KEY_PLAY_MODE, PlayerParams.VALUE_PLAYER_ACTION_LIVE);
        mBundle.putString(PlayerParams.KEY_PLAY_ACTIONID, mLiveId);
        mBundle.putString(PlayerParams.KEY_PLAY_PU, "0");
        mBundle.putBoolean(PlayerParams.KEY_PLAY_USEHLS, false);
        mBundle.putString(PlayerParams.KEY_ACTION_CUID, "");
        mBundle.putString(PlayerParams.KEY_ACTION_UTOKEN, "");
        //是否全景
        mBundle.putBoolean(VideoPlayFragment.KEY_IS_PANORAMA, false);
        mBundle.putBoolean(VideoPlayFragment.KEY_HAS_SKIN, true);
        return mBundle;
    }
//    @Override
//    public void onConfigurationChanged(Configuration newConfig) {
//        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
//            Log.d(TAG, "横屏");
//        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
//            Log.d(TAG, "竖屏");
//        }
//    }
}
