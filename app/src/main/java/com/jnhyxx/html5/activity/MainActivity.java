package com.jnhyxx.html5.activity;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.webkit.WebView;

import com.jnhyxx.html5.BuildConfig;
import com.jnhyxx.html5.R;
import com.jnhyxx.html5.fragment.AccountFragment;
import com.jnhyxx.html5.fragment.HallFragment;
import com.jnhyxx.html5.fragment.InfoFragment;
import com.jnhyxx.html5.fragment.UpgradeDialog;
import com.jnhyxx.html5.net.APIBase;
import com.jnhyxx.html5.utils.Network;
import com.jnhyxx.html5.utils.NotificationUtil;
import com.jnhyxx.html5.utils.ToastUtil;
import com.jnhyxx.html5.utils.UpgradeUtil;
import com.jnhyxx.html5.view.BottomTabs;
import com.umeng.message.IUmengRegisterCallback;
import com.umeng.message.PushAgent;

import java.net.URISyntaxException;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.jnhyxx.html5.utils.Network.registerNetworkChangeReceiver;
import static com.jnhyxx.html5.utils.Network.unregisterNetworkChangeReceiver;

public class MainActivity extends BaseActivity {

    @BindView(R.id.bottomTabs)
    BottomTabs mBottomTabs;
    @BindView(R.id.viewPager)
    ViewPager mViewPager;

    private MainFragmentsAdapter mMainFragmentsAdapter;

    private BroadcastReceiver mNetworkChangeReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        checkVersion();
        initPush();

        initView();

        processIntent(getIntent());

        mNetworkChangeReceiver = new NetworkReceiver();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mViewPager.clearOnPageChangeListeners();
    }

    private void initView() {
        mMainFragmentsAdapter = new MainFragmentsAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mMainFragmentsAdapter);
        mViewPager.setOffscreenPageLimit(1);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                mBottomTabs.selectTab(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        mViewPager.setCurrentItem(0);
        mBottomTabs.setOnTabClickListener(new BottomTabs.OnTabClickListener() {
            @Override
            public void onTabClick(int position) {
                mBottomTabs.selectTab(position);
                mViewPager.setCurrentItem(position, false);
            }
        });
    }

    private void processIntent(Intent intent) {
        final String messageId = intent.getStringExtra(NotificationUtil.KEY_MESSAGE_ID);
//        if (!TextUtils.isEmpty(messageId)) {
//            mHandler.post(new Runnable() {
//                @Override
//                public void run() {
//                    mWebView.loadUrl(APIBase.getMessageDetail(messageId));
//                }
//            });
//            return;
//        }
//
//        final String messageType = intent.getStringExtra(NotificationUtil.KEY_MESSAGE_TYPE);
//        if (!TextUtils.isEmpty(messageType)) {
//            mHandler.post(new Runnable() {
//                @Override
//                public void run() {
//                    mWebView.loadUrl(APIBase.getMessageList(messageType));
//                }
//            });
//        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        processIntent(intent);
    }

    private void initPush() {
        PushAgent pushAgent = PushAgent.getInstance(this);
        pushAgent.setDebugMode(BuildConfig.DEBUG);
        pushAgent.setMessageChannel(APIBase.HOST);
        pushAgent.setResourcePackageName("com.jnhyxx.html5");
        pushAgent.enable(new IUmengRegisterCallback() {
            @Override
            public void onRegistered(String s) {
                Log.d(TAG, "onRegistered: " + s);
            }
        });
    }

    private void checkVersion() {
        UpgradeUtil.log(this);
        if (UpgradeUtil.hasNewVersion(this)) {
            boolean forceUpgrade = UpgradeUtil.isForceUpgrade(this);
            DialogFragment dialogFragment = UpgradeDialog.newInstance(forceUpgrade);
            dialogFragment.show(getSupportFragmentManager(), "upgrade");
        }
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        registerNetworkChangeReceiver(this, mNetworkChangeReceiver);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterNetworkChangeReceiver(this, mNetworkChangeReceiver);
    }

    private void openQQChat(WebView webView, String url) {
        try {
            Intent intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME);
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            } else {
                ToastUtil.show(R.string.install_qq_first);
            }
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        while (webView.canGoBack()) {
            webView.goBack();
        }
    }

    private void openAlipay(WebView webView, String url) {
        try {
            Intent intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME);
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            }
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    private class NetworkReceiver extends Network.NetworkChangeReceiver {

        @Override
        protected void onNetworkChanged(int availableNetworkType) {
            if (availableNetworkType > Network.NET_NONE) {

            }
        }
    }

    private class MainFragmentsAdapter extends FragmentPagerAdapter {

        public MainFragmentsAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new HallFragment();
                case 1:
                    return new InfoFragment();
                case 2:
                    return new AccountFragment();
            }
            return null;
        }

        @Override
        public int getCount() {
            return 3;
        }
    }
}
