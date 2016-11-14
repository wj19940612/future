package com.jnhyxx.html5.activity;

import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jnhnxx.livevideo.LivePlayer;
import com.jnhnxx.livevideo.LivePlayerController;
import com.jnhyxx.html5.R;
import com.jnhyxx.html5.domain.live.LiveMessage;
import com.jnhyxx.html5.domain.local.LocalUser;
import com.jnhyxx.html5.domain.local.ProductPkg;
import com.jnhyxx.html5.domain.market.Product;
import com.jnhyxx.html5.domain.market.ServerIpPort;
import com.jnhyxx.html5.domain.order.ExchangeStatus;
import com.jnhyxx.html5.domain.order.HomePositions;
import com.jnhyxx.html5.fragment.live.LiveInteractionFragment;
import com.jnhyxx.html5.fragment.live.TeacherGuideFragment;
import com.jnhyxx.html5.net.API;
import com.jnhyxx.html5.net.Callback2;
import com.jnhyxx.html5.net.Resp;
import com.jnhyxx.html5.netty.NettyClient;
import com.jnhyxx.html5.netty.NettyHandler;
import com.jnhyxx.html5.utils.transform.CircleTransform;
import com.jnhyxx.html5.view.SlidingTabLayout;
import com.jnhyxx.html5.view.TitleBar;
import com.johnz.kutils.Launcher;
import com.johnz.kutils.net.CookieManger;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class LiveActivity extends LiveVideoActivity {

    @BindView(R.id.slidingTabLayout)
    SlidingTabLayout mSlidingTabLayout;
    @BindView(R.id.viewPager)
    ViewPager mViewPager;
    @BindView(R.id.titleBar)
    TitleBar mTitleBar;

    @BindView(R.id.videoView)
    LivePlayer mVideoView;
    @BindView(R.id.bufferingPrompt)
    LinearLayout mBufferingPrompt;

    @BindView(R.id.publicNoticeArea)
    LinearLayout mPublicNoticeArea;
    @BindView(R.id.publicNotice)
    TextView mPublicNotice;

    @BindView(R.id.teacherHead)
    ImageView mTeacherHead;
    @BindView(R.id.teacherCommand)
    TextView mTeacherCommand;
    @BindView(R.id.teacherCommandArea)
    LinearLayout mTeacherCommandArea;

    private List<ProductPkg> mProductPkgList = new ArrayList<>();
    private List<Product> mProductList;
    private List<HomePositions.IntegralOpSBean> mSimulationPositionList;

    private LiveMessage mLiveMessage;
    private ServerIpPort mServerIpPort;

    private NettyHandler mNettyHandler = new NettyHandler() {
        @Override
        protected void onReceiveOriginalData(String data) {
            Log.d("TAG", "onReceiveOriginalData: " + data);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live);
        ButterKnife.bind(this);

        initTitleBar();
        initVideoPlayer();
        initSlidingTabLayout();

        getChattingIpPort();
        getLiveMessage();
    }

    private void initVideoPlayer() {
        LivePlayerController playerController = new LivePlayerController(this);
        mVideoView.setPlayerController(playerController);
        mVideoView.setBufferView(mBufferingPrompt);
    }

//    private void initVideoView() {
//        mVideoPath = "http://flvdl18cf21ad.live.126.net/live/99c60b27b4154734822974a95381904c.flv?netease=flvdl18cf21ad.live.126.net";
//
//        NEMediaController mediaController = new NEMediaController(this);
//        mVideoView.setMediaController(mediaController);
//        mVideoView.setBufferStrategy(0); //直播低延时
//        mVideoView.setBufferingIndicator(mBufferingPrompt);
//        mVideoView.setMediaType(MEDIA_TYPE);
//        mVideoView.setHardwareDecoder(HARDWARE_DECODE);
//        mVideoView.setPauseInBackground(PAUSE_IN_BACKGROUND);
//        mVideoView.setVideoPath(mVideoPath);
//        // mMediaPlayer.setLogLevel(NELP_LOG_SILENT); //设置log级别
//        mVideoView.requestFocus();
//        mVideoView.start();
//    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mVideoView.isPaused()) {
            mVideoView.start();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mVideoView.pause(); //锁屏时暂停
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
        mTeacherHead.setVisibility(View.VISIBLE);
        Picasso.with(getActivity()).load(teacher.getPictureUrl())
                .transform(new CircleTransform()).into(mTeacherHead);
        connectRTMPServer(mLiveMessage.getActive());
    }

    private void showNoLiveViews() {
        mPublicNoticeArea.setVisibility(View.VISIBLE);
        mPublicNotice.setText(mLiveMessage.getNotice().getFormattedContent());
        mPublicNotice.setMovementMethod(new ScrollingMovementMethod());
    }

    private void connectRTMPServer(LiveMessage.ActiveInfo active) {
        Log.d(TAG, "connectRTMPServer: ");
        String videoPath = "http://flvdl18cf21ad.live.126.net/live/99c60b27b4154734822974a95381904c.flv?netease=flvdl18cf21ad.live.126.net";
        mVideoView.setVideoPath(videoPath);
        return;
//
//        if (!TextUtils.isEmpty(activeInfo.getRtmp())) {
//            mVideoView.setVideoPath(activeInfo.getRtmp());
//        }
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

    private void setTitleBarCustomView() {
        View customView = mTitleBar.getCustomView();
        LinearLayout liveProgramme = (LinearLayout) customView.findViewById(R.id.liveProgramme);
        ImageView programmeArrow = (ImageView) customView.findViewById(R.id.programmeArrow);
        liveProgramme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    private void openTradePage() {
        // TODO: 2016/11/8 如果没有持仓，则进入美原油  如果持仓，则进入有持仓的品种
        boolean userHasHolding = false;
        if (userHasHolding) {

        } else {
            requestProductList();
            // requestSimulationPositions(); // TODO: 09/11/2016 不是获取模拟持仓 修改
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Log.d(TAG, "横屏");
            mTitleBar.setVisibility(View.GONE);
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            Log.d(TAG, "竖屏");
            mTitleBar.setVisibility(View.VISIBLE);
        }
    }

    private void requestProductList() {
        API.Market.getProductList().setTag(TAG)
                .setCallback(new Callback2<Resp<List<Product>>, List<Product>>() {
                    @Override
                    public void onRespSuccess(List<Product> products) {
                        mProductList = products;
                        ProductPkg.updateProductPkgList(mProductPkgList, products, mSimulationPositionList, null);

                        if (mProductPkgList != null && !mProductPkgList.isEmpty()) {
                            //如果没有持仓  默认进入美原油
                            int crudeId = 1;
                            for (int i = 0; i < mProductPkgList.size(); i++) {
                                if (Product.US_CRUDE_ID == mProductPkgList.get(i).getProduct().getVarietyId()) {
                                    crudeId = i;
                                    break;
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


    private void requestSimulationPositions() {
        if (LocalUser.getUser().isLogin()) {
            API.Order.getHomePositions().setTag(TAG)
                    .setCallback(new Callback2<Resp<HomePositions>, HomePositions>() {
                        @Override
                        public void onRespSuccess(HomePositions homePositions) {
                            mSimulationPositionList = homePositions.getIntegralOpS();
                            boolean updateProductList =
                                    ProductPkg.updatePositionInProductPkg(mProductPkgList, mSimulationPositionList);
//                            if (updateProductList) {
//                                requestProductList();
//                            }
                        }
                    }).fire();
        } else { // clearHoldingOrderList all product position
            ProductPkg.clearPositions(mProductPkgList);
        }
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
                    return new LiveInteractionFragment();
                case 1:
                    return new TeacherGuideFragment();
            }
            return null;
        }

        @Override
        public int getCount() {
            return 2;
        }
    }
}
