package com.jnhyxx.html5.activity;

import android.graphics.PixelFormat;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.jnhyxx.html5.R;
import com.jnhyxx.html5.fragment.live.VideoPlayFragment;
import com.jnhyxx.html5.utils.InitLiveSdk;
import com.jnhyxx.html5.view.SlidingTabLayout;
import com.lecloud.sdk.constant.PlayerParams;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LiveActivity extends BaseActivity {

    @BindView(R.id.liveLayout)
    LinearLayout mLiveLayout;
    @BindView(R.id.slidingTabLayout)
    SlidingTabLayout mSlidingTabLayout;
    @BindView(R.id.viewPager)
    ViewPager mViewPager;
    @BindView(R.id.activity_live)
    LinearLayout mActivityLive;
    private FragmentManager mSupportFragmentManager;

    private String mLiveId = "A2016080200000n1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setFormat(PixelFormat.TRANSLUCENT);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live);
        ButterKnife.bind(this);

        if (!InitLiveSdk.cdeInitSuccess) {
            Toast.makeText(this, "CDE未初始化完成,不能播放...", Toast.LENGTH_SHORT).show();
            return;
        } else {
            Bundle bundle = setBundle();
            VideoPlayFragment videoPlayFragment = VideoPlayFragment.newInstance(bundle);
            mSupportFragmentManager = getSupportFragmentManager();
            FragmentTransaction mFragmentTransaction = mSupportFragmentManager.beginTransaction();
            mFragmentTransaction.replace(R.id.liveLayout, videoPlayFragment).commitAllowingStateLoss();
        }
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
}
