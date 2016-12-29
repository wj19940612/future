package com.jnhyxx.html5.activity.account;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;

import com.jnhyxx.html5.R;
import com.jnhyxx.html5.activity.BaseActivity;
import com.jnhyxx.html5.domain.local.LocalUser;
import com.jnhyxx.html5.domain.msg.SysMessage;
import com.jnhyxx.html5.fragment.MsgListFragment;
import com.jnhyxx.html5.fragment.TradeHintListFragment;
import com.jnhyxx.html5.utils.UmengCountEventIdUtils;
import com.jnhyxx.html5.view.SlidingTabLayout;
import com.jnhyxx.html5.view.dialog.SmartDialog;
import com.johnz.kutils.Launcher;
import com.umeng.analytics.MobclickAgent;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MessageCenterActivity extends BaseActivity implements MsgListFragment.OnMsgItemClickListener {

    @BindView(R.id.slidingTabLayout)
    SlidingTabLayout mSlidingTabLayout;
    @BindView(R.id.viewPager)
    ViewPager mViewPager;
    private MessagePagesAdapter mMessagePagesAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_center);
        ButterKnife.bind(this);

        mViewPager.setOffscreenPageLimit(2);
        mViewPager.setCurrentItem(0, false);
        mMessagePagesAdapter = new MessagePagesAdapter(getSupportFragmentManager(), MessageCenterActivity.this);
        mViewPager.setAdapter(mMessagePagesAdapter);

        mSlidingTabLayout.setDistributeEvenly(true);
        mSlidingTabLayout.setDividerColors(ContextCompat.getColor(MessageCenterActivity.this, android.R.color.transparent));
        mSlidingTabLayout.setViewPager(mViewPager);

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 1) {
                    MobclickAgent.onEvent(getActivity(), UmengCountEventIdUtils.TRADE_HINT);
                    if (!LocalUser.getUser().isLogin()) {
                        SmartDialog.with(getActivity(), getString(R.string.no_login))
                                .setCancelableOnTouchOutside(false)
                                .setNegative(R.string.cancel, new SmartDialog.OnClickListener() {
                                    @Override
                                    public void onClick(Dialog dialog) {
                                        dialog.dismiss();
                                        mViewPager.setCurrentItem(0);
                                    }
                                })
                                .setPositive(R.string.sign_in, new SmartDialog.OnClickListener() {
                                    @Override
                                    public void onClick(Dialog dialog) {
                                        dialog.dismiss();
                                        Launcher.with(getActivity(), SignInActivity.class)
                                                .executeForResult(REQ_CODE_LOGIN);
                                    }
                                }).show();
                    }
                }else {
                    MobclickAgent.onEvent(getActivity(), UmengCountEventIdUtils.SYSTEM_MESSAGE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_CODE_LOGIN && resultCode == RESULT_OK) {
            TradeHintListFragment fragment = (TradeHintListFragment) mMessagePagesAdapter.getFragment(1);
            if (fragment != null) {
                fragment.requestMessageList();
            }
        }
    }

    class MessagePagesAdapter extends FragmentPagerAdapter {

        private Context mContext;
        FragmentManager mFragmentManager;

        public MessagePagesAdapter(FragmentManager fm, Context context) {
            super(fm);
            mContext = context;
            mFragmentManager = fm;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return mContext.getString(R.string.system_message);
                case 1:
                    return mContext.getString(R.string.trade_remind);
            }
            return super.getPageTitle(position);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return MsgListFragment.newInstance(MsgListFragment.TYPE_SYSTEM);
                case 1:
                    return TradeHintListFragment.newInstance(TradeHintListFragment.TYPE_TRADE);
            }
            return null;
        }

        @Override
        public int getCount() {
            return 2;
        }

        public Fragment getFragment(int position) {
            return mFragmentManager.findFragmentByTag("android:switcher:" + R.id.viewPager + ":" + position);
        }
    }

    @Override
    public void onMsgItemClick(SysMessage sysMessage) {
        MobclickAgent.onEvent(getActivity(), UmengCountEventIdUtils.SYSTEM_MESSAGE_DETAILS);
        Launcher.with(getActivity(), MessageCenterListItemInfoActivity.class)
                .putExtra(Launcher.EX_PAYLOAD, sysMessage).execute();
    }
}
