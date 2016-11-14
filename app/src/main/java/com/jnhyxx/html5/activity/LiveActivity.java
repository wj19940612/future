package com.jnhyxx.html5.activity;

import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.jnhyxx.html5.R;
import com.jnhyxx.html5.domain.live.LiveMessage;
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
import com.jnhyxx.html5.net.Callback;
import com.jnhyxx.html5.net.Callback2;
import com.jnhyxx.html5.net.Resp;
import com.jnhyxx.html5.netty.NettyClient;
import com.jnhyxx.html5.netty.NettyHandler;
import com.jnhyxx.html5.utils.VideoLayoutParams;
import com.jnhyxx.html5.view.LiveProgramDir;
import com.jnhyxx.html5.view.SlidingTabLayout;
import com.jnhyxx.html5.view.TitleBar;
import com.johnz.kutils.Launcher;
import com.johnz.kutils.net.CookieManger;
import com.lecloud.sdk.videoview.IMediaDataVideoView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class LiveActivity extends LiveVideoActivity implements View.OnClickListener {

    @BindView(R.id.liveLayout)
    RelativeLayout mLiveLayout;
    @BindView(R.id.slidingTabLayout)
    SlidingTabLayout mSlidingTabLayout;
    @BindView(R.id.viewPager)
    ViewPager mViewPager;
    @BindView(R.id.titleBar)
    TitleBar mTitleBar;
    @BindView(R.id.liveProgramDir)
    FrameLayout mLiveProgramDir;

//    //    老师指令布局
//    @BindView(R.id.teacherGuideLayout)
//    RelativeLayout mTeacherGuideLayout;
//    @BindView(R.id.teacherHeadImage)
//    CircularAnnulusImageView mTeacherHeadImage;
//    @BindView(R.id.teacherGuideContent)
//    TextView mTeacherGuideContent;


    // TODO: 2016/11/8 房间Id 
//    private String mLiveId = "A2016080200000n1";
    private String mLiveId = "A2016053100000je";


    private List<ProductPkg> mProductPkgList = new ArrayList<>();
    private List<Product> mProductList;
    private List<HomePositions.IntegralOpSBean> mSimulationPositionList;

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
        }
    };

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live);
        ButterKnife.bind(this);
        mLiveInteractionFragment = LiveInteractionFragment.newInstance();
        initTitleBar();
        initVideoView();
        getLiveMessage();
        initSlidingTabLayout();

        getChattingIpPort();

        setLayoutData();

    }

    private void setLayoutData() {
//        if (mLiveMessage == null) return;
//        LiveMessage.TeacherInfo teacher = mLiveMessage.getTeacher();
//        if (teacher != null) {
//            if (!TextUtils.isEmpty(teacher.getPictureUrl())) {
//                Picasso.with(getActivity()).load(teacher.getPictureUrl()).into(mTeacherHeadImage);
//            }
//            //老师指令内容
//            mTeacherGuideContent.setText(teacher.getAccount());
//        }
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

    @Override
    protected void onAddViewView(IMediaDataVideoView videoView) {
        mLiveLayout.addView((View) videoView, VideoLayoutParams.computeContainerSize(this, 16, 9));
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
                    }
                }).fire();
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
        LiveTeacherInfoDialogFragment liveTeacherInfoDialogFragment = LiveTeacherInfoDialogFragment.newInstance(mLiveMessage.getTeacher());
        liveTeacherInfoDialogFragment.show(getSupportFragmentManager());
    }

    private void setTitleBarCustomView() {
        View customView = mTitleBar.getCustomView();
        LinearLayout liveProgramme = (LinearLayout) customView.findViewById(R.id.liveProgramme);
        liveProgramme.setClickable(true);
        liveProgramme.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.liveProgramme:
                LiveProgramDir.showLiveProgramDirPopupWindow(getActivity(), mLiveMessage.getProgram(), mTitleBar);
                // TODO: 2016/11/10 ，目前先显示老师详情
                showTeacherInfoDialog();
                break;
        }
    }

    private void openTradePage() {
        //获取用户持仓数据
        requestUserPositions();
    }

    private boolean userHasPositions(HomePositions mHomePositions) {
        if (mHomePositions != null && !mHomePositions.getCashOpS().isEmpty()) {
            return true;
        }
        return false;
    }

    private void requestUserPositions() {
        if (LocalUser.getUser().isLogin()) {
            API.Order.getHomePositions().setTag(TAG)
                    .setCallback(new Callback<Resp<HomePositions>>(false) {
                        @Override
                        public void onSuccess(Resp<HomePositions> homePositionsResp) {
                            Log.d("VolleyHttp", getUrl() + " onSuccess: " + homePositionsResp.toString());
                            if (homePositionsResp.isSuccess()) {
                                HomePositions mHomePositions = homePositionsResp.getData();
                                boolean userHasPositions = userHasPositions(mHomePositions);
                                requestProductList(userHasPositions, mHomePositions);
                            }
                        }

                        @Override
                        public void onReceive(Resp<HomePositions> homePositionsResp) {
                        }
                    }).fire();
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

    private void requestProductList(final boolean hasPositions, final HomePositions mHomePositions) {
        API.Market.getProductList().setTag(TAG)
                .setCallback(new Callback2<Resp<List<Product>>, List<Product>>() {
                    @Override
                    public void onRespSuccess(List<Product> products) {
                        mProductList = products;
                        ProductPkg.updateProductPkgList(mProductPkgList, products, mSimulationPositionList, null);

                        if (mProductPkgList != null && !mProductPkgList.isEmpty()) {
                            //如果没有持仓  默认进入美原油,如果有持仓,进入持仓界面
                            int crudeId = 1;
                            if (hasPositions) {
                                String varietyType = mHomePositions.getCashOpS().get(0).getVarietyType();
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
