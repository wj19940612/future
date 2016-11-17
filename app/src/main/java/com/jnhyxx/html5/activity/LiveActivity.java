package com.jnhyxx.html5.activity;

import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.jnhnxx.livevideo.LiveVideo;
import com.jnhyxx.html5.R;
import com.jnhyxx.html5.domain.live.ChatData;
import com.jnhyxx.html5.domain.live.LiveMessage;
import com.jnhyxx.html5.domain.live.LiveSpeakInfo;
import com.jnhyxx.html5.domain.local.LocalUser;
import com.jnhyxx.html5.domain.local.ProductPkg;
import com.jnhyxx.html5.domain.market.Product;
import com.jnhyxx.html5.domain.market.ServerIpPort;
import com.jnhyxx.html5.domain.order.ExchangeStatus;
import com.jnhyxx.html5.domain.order.HomePositions;
import com.jnhyxx.html5.fragment.live.LiveInteractionFragment;
import com.jnhyxx.html5.fragment.live.LiveTeacherInfoDialogFragment;
import com.jnhyxx.html5.fragment.live.TeacherGuideFragment;
import com.jnhyxx.html5.net.API;
import com.jnhyxx.html5.net.Callback1;
import com.jnhyxx.html5.net.Callback2;
import com.jnhyxx.html5.net.Resp;
import com.jnhyxx.html5.netty.NettyClient;
import com.jnhyxx.html5.netty.NettyHandler;
import com.jnhyxx.html5.view.LiveProgramDir;
import com.jnhyxx.html5.view.SlidingTabLayout;
import com.jnhyxx.html5.view.TeacherCommand;
import com.jnhyxx.html5.view.TitleBar;
import com.johnz.kutils.Launcher;
import com.johnz.kutils.net.CookieManger;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class LiveActivity extends BaseActivity {

    @BindView(R.id.slidingTabLayout)
    SlidingTabLayout mSlidingTabLayout;
    @BindView(R.id.viewPager)
    ViewPager mViewPager;
    @BindView(R.id.titleBar)
    TitleBar mTitleBar;

    @BindView(R.id.liveVideo)
    LiveVideo mLivePlayer;
    @BindView(R.id.videoContainer)
    RelativeLayout mVideoContainer;

    @BindView(R.id.publicNoticeArea)
    LinearLayout mPublicNoticeArea;
    @BindView(R.id.publicNotice)
    TextView mPublicNotice;

    @BindView(R.id.teacherCommand)
    TeacherCommand mTeacherCommand;

    private List<ProductPkg> mProductPkgList = new ArrayList<>();
    private List<Product> mProductList;

    private LiveMessage mLiveMessage;
    private ServerIpPort mServerIpPort;

    private LiveInteractionFragment mLiveInteractionFragment;

    private NettyHandler mNettyHandler = new NettyHandler() {
        @Override
        protected void onReceiveOriginalData(String data) {
            Log.d(TAG, "onReceiveOriginalData: " + data);
            if (mLiveInteractionFragment != null) {
                mLiveInteractionFragment.setData(data);
            }

            LiveSpeakInfo liveSpeakInfo = new Gson().fromJson(data, LiveSpeakInfo.class);
            ChatData chatData = new ChatData(liveSpeakInfo);
            if (chatData.getChatType() == ChatData.CHAT_TYPE_TEACHER && chatData.isOrder()) {
                setTeacherCommand(chatData);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live);
        ButterKnife.bind(this);

        mLiveInteractionFragment = LiveInteractionFragment.newInstance();

        initTitleBar();
        initSlidingTabLayout();

        mTeacherCommand.setOnTeacherHeadClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTeacherInfoDialog();
            }
        });

        getLiveMessage();
        getChattingIpPort();
        getLastTeacherCommand();
    }

    private void getLastTeacherCommand() {
        API.Live.getLastTeacherGuide().setTag(TAG)
                .setCallback(new Callback2<Resp<ChatData>, ChatData>() {
                    @Override
                    public void onRespSuccess(ChatData chatData) {
                        setTeacherCommand(chatData);
                    }
                }).fire();
    }

    private void setTeacherCommand(ChatData chatData) {
        if (chatData != null && !TextUtils.isEmpty(chatData.getMsg())) {
            mTeacherCommand.setTeacherCommand(chatData.getMsg());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        disconnectNettySocket();
    }

    private void getChattingIpPort() {
        API.Market.getChattingServerIpAndPort().setTag(TAG)
                .setCallback(new Callback2<Resp<List<ServerIpPort>>, List<ServerIpPort>>() {
                    @Override
                    public void onRespSuccess(List<ServerIpPort> serverIpPorts) {
                        if (serverIpPorts != null && serverIpPorts.size() > 0) {
                            mServerIpPort = serverIpPorts.get(0);
                            if (mLiveMessage != null) {
                                connectNettySocket();
                            }
                        }
                    }
                }).fire();
    }

    private void getLiveMessage() {
        API.Live.getLiveMessage().setTag(TAG).setIndeterminate(this)
                .setCallback(new Callback2<Resp<LiveMessage>, LiveMessage>() {
                    @Override
                    public void onRespSuccess(LiveMessage liveMessage) {

                        mLiveMessage = liveMessage;

                        if (mServerIpPort != null) {
                            connectNettySocket();
                        }

                        if (mLiveMessage.getTeacher() != null) { // 在直播
                            showLiveViews();
                        } else if (mLiveMessage.getNotice() != null) { // 未直播,显示通告
                            showNoLiveViews();
                        }
                    }
                }).fire();
    }

    private void showLiveViews() {
        LiveMessage.TeacherInfo teacher = mLiveMessage.getTeacher();
        mPublicNoticeArea.setVisibility(View.GONE);
        mTeacherCommand.setVisibility(View.VISIBLE);
        mTeacherCommand.setTeacherHeader(teacher.getPictureUrl());
        connectRTMPServer(mLiveMessage.getActive());
    }

    private void showNoLiveViews() {
        mPublicNoticeArea.setVisibility(View.VISIBLE);
        mPublicNotice.setText(mLiveMessage.getNotice().getFormattedContent());
        mPublicNotice.setMovementMethod(new ScrollingMovementMethod());
        mTeacherCommand.setVisibility(View.GONE);
    }

    private void connectRTMPServer(LiveMessage.ActiveInfo active) {
        Log.d(TAG, "connectRTMPServer: ");
        if (!TextUtils.isEmpty(active.getRtmp())) {
            mLivePlayer.setVideoPath(active.getRtmp());
        }
    }

    private void connectNettySocket() {
        if (mLiveMessage.getTeacher() != null) {
            int teacherId = mLiveMessage.getTeacher().getTeacherAccountId();
            NettyClient.getInstance().setIpAndPort(mServerIpPort.getIp(), mServerIpPort.getPort());
            NettyClient.getInstance().start(teacherId, CookieManger.getInstance().getCookies());
        }
        NettyClient.getInstance().addNettyHandler(mNettyHandler);
    }

    private void disconnectNettySocket() {
        NettyClient.getInstance().stop();
        NettyClient.getInstance().removeNettyHandler(mNettyHandler);
    }

    private void initSlidingTabLayout() {
        mSlidingTabLayout.setDistributeEvenly(true);
        mSlidingTabLayout.setDividerColors(ContextCompat.getColor(LiveActivity.this, android.R.color.transparent));
        mViewPager.setAdapter(new LivePageFragmentAdapter(getSupportFragmentManager()));
        mSlidingTabLayout.setViewPager(mViewPager);
    }

    private void initTitleBar() {
        mTitleBar.setOnRightViewClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openTradePage();
            }
        });
        setTitleBarCustomView();
    }

    private void showTeacherInfoDialog() {
        LiveMessage.TeacherInfo teacherInfo = mLiveMessage.getTeacher();
        if (teacherInfo != null) {
            LiveTeacherInfoDialogFragment liveTeacherInfoDialogFragment
                    = LiveTeacherInfoDialogFragment.newInstance(teacherInfo);
            liveTeacherInfoDialogFragment.show(getSupportFragmentManager());
        }
    }

    private void setTitleBarCustomView() {
        View customView = mTitleBar.getCustomView();
        LinearLayout liveProgramme = (LinearLayout) customView.findViewById(R.id.liveProgramme);
        liveProgramme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LiveProgramDir.showLiveProgramDirPopupWindow(getActivity(), mLiveMessage.getProgram(), mTitleBar);
            }
        });
    }

    private void openTradePage() {
        //获取用户持仓数据
        requestUserPositions();
    }

    private boolean ifUserHasPositions(HomePositions homePositions) {
        if (homePositions != null && homePositions.getCashOpS() != null && !homePositions.getCashOpS().isEmpty()) {
            return true;
        }
        return false;
    }

    private void requestUserPositions() {
        if (LocalUser.getUser().isLogin()) {
            API.Order.getHomePositions().setTag(TAG)
                    .setCallback(new Callback1<Resp<HomePositions>>() {

                        @Override
                        protected void onRespSuccess(Resp<HomePositions> resp) {
                            if (resp.isSuccess() && resp.hasData()) {
                                HomePositions mHomePositions = resp.getData();
                                boolean userHasPositions = ifUserHasPositions(mHomePositions);
                                requestProductList(userHasPositions, mHomePositions);
                            }
                        }
                    }).fire();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            mTitleBar.setVisibility(View.GONE);
            mTeacherCommand.setVisibility(View.GONE);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);
            mVideoContainer.setLayoutParams(params);
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            mTitleBar.setVisibility(View.VISIBLE);
            mTeacherCommand.setVisibility(View.VISIBLE);
            int containerHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 203,
                    getResources().getDisplayMetrics());
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    containerHeight);
            mVideoContainer.setLayoutParams(params);
        }
    }

    private void requestProductList(final boolean hasPositions, final HomePositions homePositions) {
        API.Market.getProductList().setTag(TAG)
                .setCallback(new Callback2<Resp<List<Product>>, List<Product>>() {
                    @Override
                    public void onRespSuccess(List<Product> products) {
                        mProductList = products;
                        ProductPkg.updateProductPkgList(mProductPkgList, products, null, null);

                        if (mProductPkgList != null && !mProductPkgList.isEmpty()) {
                            // 如果没有持仓  默认进入美原油交易界面, 如果有持仓, 进入有持仓的产品交易界面
                            int crudeId = 1;
                            if (hasPositions) {
                                String varietyType = homePositions.getCashOpS().get(0).getVarietyType();
                                for (int i = 0; i < mProductPkgList.size(); i++) {
                                    if (varietyType.equalsIgnoreCase(mProductPkgList.get(i).getProduct().getVarietyType())) {
                                        crudeId = i;
                                        break;
                                    }
                                }
                            } else {
                                for (int i = 0; i < mProductPkgList.size(); i++) {
                                    if (Product.US_CRUDE_ID == mProductPkgList.get(i).getProduct().getVarietyId()) {
                                        crudeId = i;
                                        break;
                                    }
                                }
                            }
                            ProductPkg productPkg = mProductPkgList.get(crudeId);
                            requestServerIpAndPort(productPkg);
                        }
                    }
                }).fire();
    }


    private void requestServerIpAndPort(final ProductPkg productPkg) {
        API.Market.getMarketServerIpAndPort().setTag(TAG)
                .setCallback(new Callback2<Resp<List<ServerIpPort>>, List<ServerIpPort>>() {
                    @Override
                    public void onRespSuccess(List<ServerIpPort> marketServers) {
                        if (marketServers != null && marketServers.size() > 0) {
                            requestProductExchangeStatus(productPkg.getProduct(), marketServers);
                        }
                    }
                }).fire();
    }

    private void requestProductExchangeStatus(final Product product, final List<ServerIpPort> marketServers) {
        API.Order.getExchangeTradeStatus(product.getExchangeId(), product.getVarietyType())
                .setTag(TAG)
                .setCallback(new Callback2<Resp<ExchangeStatus>, ExchangeStatus>() {
                    @Override
                    public void onRespSuccess(ExchangeStatus exchangeStatus) {
                        product.setExchangeStatus(exchangeStatus.isTradeable()
                                ? Product.MARKET_STATUS_OPEN : Product.MARKET_STATUS_CLOSE);

                        Launcher.with(LiveActivity.this, TradeActivity.class)
                                .putExtra(Product.EX_PRODUCT, product)
                                .putExtra(Product.EX_FUND_TYPE, Product.FUND_TYPE_CASH)
                                .putExtra(Product.EX_PRODUCT_LIST, new ArrayList<>(mProductList))
                                .putExtra(ExchangeStatus.EX_EXCHANGE_STATUS, exchangeStatus)
                                .putExtra(ServerIpPort.EX_IP_PORTS, new ArrayList<Parcelable>(marketServers))
                                .execute();
                    }
                }).fire();
    }

    private class LivePageFragmentAdapter extends FragmentPagerAdapter {

        public LivePageFragmentAdapter(FragmentManager supportFragmentManager) {
            super(supportFragmentManager);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return getString(R.string.live_interaction);
                case 1:
                    return getString(R.string.live_teacher_guide);
            }
            return super.getPageTitle(position);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return mLiveInteractionFragment;
                case 1:
                    return TeacherGuideFragment.newInstance();
            }
            return null;
        }

        @Override
        public int getCount() {
            return 2;
        }
    }
}
