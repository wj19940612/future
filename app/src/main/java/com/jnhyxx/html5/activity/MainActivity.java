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

import com.jnhyxx.html5.Preference;
import com.jnhyxx.html5.R;
import com.jnhyxx.html5.activity.account.MessageCenterListItemInfoActivity;
import com.jnhyxx.html5.domain.ChannelServiceInfo;
import com.jnhyxx.html5.domain.market.ServerIpPort;
import com.jnhyxx.html5.domain.msg.SysMessage;
import com.jnhyxx.html5.fragment.HomeFragment;
import com.jnhyxx.html5.fragment.InfoFragment;
import com.jnhyxx.html5.fragment.LiveFragment;
import com.jnhyxx.html5.fragment.MarketFragment;
import com.jnhyxx.html5.fragment.MineFragment;
import com.jnhyxx.html5.fragment.dialog.UpgradeDialog;
import com.jnhyxx.html5.net.API;
import com.jnhyxx.html5.net.Callback1;
import com.jnhyxx.html5.net.Resp;
import com.jnhyxx.html5.service.PushIntentService;
import com.jnhyxx.html5.utils.UmengCountEventIdUtils;
import com.jnhyxx.html5.utils.UpgradeUtil;
import com.jnhyxx.html5.view.BottomTabs;
import com.jnhyxx.html5.view.dialog.HomePopup;
import com.johnz.kutils.Launcher;
import com.umeng.analytics.MobclickAgent;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends BaseActivity {

    @BindView(R.id.bottomTabs)
    BottomTabs mBottomTabs;
    @BindView(R.id.viewPager)
    ViewPager mViewPager;

    private MainFragmentsAdapter mMainFragmentsAdapter;

    private static final int TAB_HOME = 0;
    private static final int TAB_MARKET = 1;
    private static final int TAB_MESSAGE = 2;
    private static final int TAB_MINE = 3;

    private BroadcastReceiver mPushBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equalsIgnoreCase(PushIntentService.PUSH_ACTION)) {
                final SysMessage sysMessage = (SysMessage) intent.getSerializableExtra(PushIntentService.KEY_PUSH_DATA);
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

    public BottomTabs getBottomTabs() {
        return mBottomTabs;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        checkVersion();

        initView();

        getServiceInfo();

        ServerIpPort.requestMarketServerIpAndPort(null);
    }

    private void getServiceInfo() {
        API.User.getChannelByDomain()
                .setTag(TAG).setIndeterminate(this)
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
        mViewPager.setOffscreenPageLimit(3);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                if (position == TAB_HOME) {
                    MobclickAgent.onEvent(getActivity(), UmengCountEventIdUtils.TAB_HOME);
                } else if (position == TAB_MARKET) {

                } else if (position == TAB_MESSAGE) {
                    MobclickAgent.onEvent(getActivity(), UmengCountEventIdUtils.TAB_MESSAGE);
                } else if (position == TAB_MINE) {
                    MobclickAgent.onEvent(getActivity(), UmengCountEventIdUtils.TAB_MINE);
                }

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
        LocalBroadcastManager.getInstance(MainActivity.this).registerReceiver(mPushBroadcastReceiver, new IntentFilter(PushIntentService.PUSH_ACTION));
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
        LocalBroadcastManager.getInstance(MainActivity.this).unregisterReceiver(mPushBroadcastReceiver);
    }

    private class MainFragmentsAdapter extends FragmentPagerAdapter {

        FragmentManager mFragmentManager;

        public MainFragmentsAdapter(FragmentManager fm) {
            super(fm);
            mFragmentManager = fm;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new HomeFragment();
                case 1:
                    return new MarketFragment();
                case 2:
                    return new InfoFragment();
                case 3:
                    return new MineFragment();
            }
            return null;
        }

        @Override
        public int getCount() {
            return 4;
        }

        public Fragment getFragment(int position) {
            return mFragmentManager.findFragmentByTag("android:switcher:" + R.id.viewPager + ":" + position);
        }
    }
}
