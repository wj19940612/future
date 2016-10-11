package com.jnhyxx.html5.activity.account;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;

import com.jnhyxx.html5.R;
import com.jnhyxx.html5.activity.BaseActivity;
import com.jnhyxx.html5.domain.msg.SysTradeMessage;
import com.jnhyxx.html5.fragment.MsgListFragment;
import com.jnhyxx.html5.fragment.TradeHintListFragment;
import com.jnhyxx.html5.view.SlidingTabLayout;
import com.johnz.kutils.Launcher;

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

    }

    class MessagePagesAdapter extends FragmentPagerAdapter {

        private Context mContext;

        public MessagePagesAdapter(FragmentManager fm, Context context) {
            super(fm);
            mContext = context;
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
    }

    @Override
    public void onMsgItemClick(SysTradeMessage sysTradeMessage) {
        Launcher.with(MessageCenterActivity.this, MessageCenterListItemInfoActivity.class).putExtra(Launcher.EX_PAYLOAD, sysTradeMessage).execute();
    }
}
