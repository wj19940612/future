package com.jnhyxx.html5.activity;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.util.Log;

import com.jnhyxx.html5.Preference;
import com.jnhyxx.html5.R;
import com.jnhyxx.html5.activity.account.MessageCenterListItemInfoActivity;
import com.jnhyxx.html5.domain.ChannelServiceInfo;
import com.jnhyxx.html5.domain.market.ServerIpPort;
import com.jnhyxx.html5.domain.msg.SysMessage;
import com.jnhyxx.html5.fragment.HomeFragment;
import com.jnhyxx.html5.fragment.InfoFragment;
import com.jnhyxx.html5.fragment.MineFragment;
import com.jnhyxx.html5.fragment.dialog.UpgradeDialog;
import com.jnhyxx.html5.net.API;
import com.jnhyxx.html5.net.Callback1;
import com.jnhyxx.html5.net.Resp;
import com.jnhyxx.html5.receiver.PushReceiver;
import com.jnhyxx.html5.utils.Network;
import com.jnhyxx.html5.utils.UmengCountEventIdUtils;
import com.jnhyxx.html5.utils.UpgradeUtil;
import com.jnhyxx.html5.view.BottomTabs;
import com.jnhyxx.html5.view.dialog.HomePopup;
import com.johnz.kutils.Launcher;
import com.umeng.analytics.MobclickAgent;

import java.util.List;

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

    private int mTabPosition;
    //首页tab的position
    private static final int TAB_HOME = 0;
    //资讯tab的position,用来友盟记录第点击次数
    private static final int TAB_MESSAGE = 1;
    //我的tab
    private static final int TAB_MINE = 2;

    private static final int REQ_CODE_LIVE = 770;

    private BroadcastReceiver mPushBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equalsIgnoreCase(PushReceiver.PUSH_ACTION)) {
                final SysMessage sysMessage = (SysMessage) intent.getSerializableExtra(PushReceiver.KEY_PUSH_DATA);
                if (sysMessage != null && !Preference.get().hasShowedThisSysMessage(sysMessage)) {
                    HomePopup.with(getActivity(), sysMessage.getPushTopic(), sysMessage.getPushContent())
                            .setOnCheckDetailListener(new HomePopup.OnClickListener() {
                                @Override
                                public void onClick(Dialog dialog) {
                                    dialog.dismiss();
                                    Launcher.with(getActivity(), MessageCenterListItemInfoActivity.class)
                                            .putExtra(Launcher.EX_PAYLOAD, sysMessage).execute();
                                }
                            }).show();
                    Preference.get().setThisSysMessageShowed(sysMessage);
                }
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        checkVersion();

        initView();

        mNetworkChangeReceiver = new NetworkReceiver();

        getServiceInfo();

        ServerIpPort.requestMarketServerIpAndPort(null);
    }

    private void getServiceInfo() {
        API.User.getChannelByDomain()
                .setTag(TAG)
                .setIndeterminate(this)
                .setCallback(new Callback1<Resp<ChannelServiceInfo>>() {
                    @Override
                    protected void onRespSuccess(Resp<ChannelServiceInfo> resp) {
                        Preference preference = Preference.get();
                        preference.setServiceQQ(resp.getData().getQq());
                        preference.setServicePhone(resp.getData().getPhone());
                    }
                }).fire();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mViewPager.clearOnPageChangeListeners();
    }

    private void initView() {
        mMainFragmentsAdapter = new MainFragmentsAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mMainFragmentsAdapter);
        mViewPager.setOffscreenPageLimit(2);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                if (position == TAB_HOME) {
                    MobclickAgent.onEvent(getActivity(), UmengCountEventIdUtils.TAB_HOME);
                } else if (position == TAB_MESSAGE) {
                    MobclickAgent.onEvent(getActivity(), UmengCountEventIdUtils.TAB_MESSAGE);
                } else if (position == TAB_MINE) {
                    MobclickAgent.onEvent(getActivity(), UmengCountEventIdUtils.TAB_MINE);
                }

                if (position >= 1) {
                    mTabPosition = position + 1;
                    mBottomTabs.selectTab(mTabPosition);
                } else {
                    mTabPosition = position;
                    mBottomTabs.selectTab(mTabPosition);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        mViewPager.setCurrentItem(0);
        mBottomTabs.setOnTabClickListener(new BottomTabs.OnTabClickListener() {
            @Override
            public void onTabClick(int position) {
                if (position == BottomTabs.TAB_LIVE_INDEX) {
                    MobclickAgent.onEvent(getActivity(), UmengCountEventIdUtils.TAB_LIVE);
                }
                mBottomTabs.selectTab(position);
                if (position == 1) {
                    openLivePage();
                } else if (position >= 1) {
                    mViewPager.setCurrentItem(position - 1, false);
                } else {
                    mViewPager.setCurrentItem(position, false);
                }

            }
        });
    }

    private void openLivePage() {
        Launcher.with(getActivity(), LiveActivity.class).executeForResult(REQ_CODE_LIVE);
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
        LocalBroadcastManager.getInstance(MainActivity.this).registerReceiver(mPushBroadcastReceiver, new IntentFilter(PushReceiver.PUSH_ACTION));
        requestHomePopup();
    }

    private void requestHomePopup() {
        API.Message.getHomePopup().setTag(TAG)
                .setCallback(new Callback1<Resp<List<SysMessage>>>() {
                    @Override
                    protected void onRespSuccess(Resp<List<SysMessage>> resp) {
                        if (resp.isSuccess() && resp.hasData()) {
                            showSysMessageDialog(resp.getData().get(0));
                        }
                    }
                }).fire();
    }

    private void showSysMessageDialog(final SysMessage sysMessage) {
        Log.d(TAG, "弹窗消息  " + sysMessage.getCreateTime());
        if (!Preference.get().hasShowedThisSysMessage(sysMessage)) {
            HomePopup.with(getActivity(), sysMessage.getPushTopic(), sysMessage.getPushContent())
                    .setOnCheckDetailListener(new HomePopup.OnClickListener() {
                        @Override
                        public void onClick(Dialog dialog) {
                            dialog.dismiss();
                            Launcher.with(getActivity(), MessageCenterListItemInfoActivity.class)
                                    .putExtra(Launcher.EX_PAYLOAD, sysMessage).execute();
                        }
                    }).show();
            Preference.get().setThisSysMessageShowed(sysMessage);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterNetworkChangeReceiver(this, mNetworkChangeReceiver);
        LocalBroadcastManager.getInstance(MainActivity.this).unregisterReceiver(mPushBroadcastReceiver);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_CODE_LIVE) {
            mBottomTabs.selectTab(mTabPosition);
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
                    return new HomeFragment();
                case 1:
                    return new InfoFragment();
                case 2:
                    return new MineFragment();
            }
            return null;
        }

        @Override
        public int getCount() {
            return 3;
        }
    }
}
