package com.jnhyxx.html5.activity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.os.Build;
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
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.jnhnxx.livevideo.LiveVideo;
import com.jnhyxx.html5.R;
import com.jnhyxx.html5.activity.account.SignInActivity;
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
import com.jnhyxx.html5.fragment.live.LiveTeacherInfoFragment;
import com.jnhyxx.html5.fragment.live.TeacherGuideFragment;
import com.jnhyxx.html5.net.API;
import com.jnhyxx.html5.net.Callback1;
import com.jnhyxx.html5.net.Callback2;
import com.jnhyxx.html5.net.Resp;
import com.jnhyxx.html5.netty.NettyClient;
import com.jnhyxx.html5.netty.NettyHandler;
import com.jnhyxx.html5.utils.ToastUtil;
import com.jnhyxx.html5.view.LiveProgrammeList;
import com.jnhyxx.html5.view.SlidingTabLayout;
import com.jnhyxx.html5.view.TeacherCommand;
import com.jnhyxx.html5.view.TitleBar;
import com.johnz.kutils.Launcher;
import com.johnz.kutils.net.CookieManger;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.jnhyxx.html5.R.id.videoContainer;


public class LiveActivity extends BaseActivity {

    public static final int REQUEST_CODE_LOGIN = 583;

    @BindView(R.id.slidingTabLayout)
    SlidingTabLayout mSlidingTabLayout;
    @BindView(R.id.viewPager)
    ViewPager mViewPager;
    @BindView(R.id.titleBar)
    TitleBar mTitleBar;

    @BindView(R.id.liveVideo)
    LiveVideo mLivePlayer;
    @BindView(videoContainer)
    RelativeLayout mVideoContainer;

    @BindView(R.id.publicNoticeArea)
    LinearLayout mPublicNoticeArea;
    @BindView(R.id.publicNotice)
    TextView mPublicNotice;

    @BindView(R.id.teacherCommand)
    TeacherCommand mTeacherCommand;
    @BindView(R.id.dimBackground)
    LinearLayout mDimBackground;


    @BindView(R.id.speakEditText)
    EditText mSpeakEditText;
    @BindView(R.id.sendSpeak)
    TextView mSendSpeak;
    @BindView(R.id.speakLayout)
    LinearLayout mSpeakLayout;
    @BindView(R.id.liveSpeak)
    ImageView mLiveSpeak;


    private LiveProgrammeList mProgrammeList;

    private List<ProductPkg> mProductPkgList = new ArrayList<>();
    private List<Product> mProductList;

    private LiveMessage mLiveMessage;
    private ServerIpPort mServerIpPort;

    private TeacherGuideFragment mTeacherGuideFragment;
    private LiveInteractionFragment mLiveInteractionFragment;


    private InputMethodManager mInputMethodManager;
    //用来记录键盘是否打开
    private boolean mKeyBoardIsOpen = false;


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

            if (chatData.isOrder()) {
                mTeacherGuideFragment.setData(chatData);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live);
        ButterKnife.bind(this);


        initData();

        initTitleBar();
        initSlidingTabLayout();

        getLiveMessage();
        getChattingIpPort();
        getLastTeacherCommand();
        mInputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        View content = getActivity().findViewById(android.R.id.content);
        content.getViewTreeObserver().addOnGlobalLayoutListener(onGlobalLayoutListener);
    }

    private void initData() {
        mLiveInteractionFragment = LiveInteractionFragment.newInstance();
        mLiveInteractionFragment.setOnScrollListener(new LiveInteractionFragment.OnScrollListener() {
            @Override
            public void scroll(boolean isScroll) {
                if (isScroll && mKeyBoardIsOpen) {
//                    mSpeakLayout.setVisibility(View.GONE);
                    if (mSpeakEditText != null) {
                        mSpeakEditText.setText("");
                    }
                    mKeyBoardIsOpen = false;
                    if (mInputMethodManager != null && mSpeakEditText != null) {
                        mInputMethodManager.hideSoftInputFromWindow(mSpeakEditText.getWindowToken(), 0);
                    }
                    mVideoContainer.setVisibility(View.VISIBLE);
                    mLiveSpeak.setVisibility(View.VISIBLE);
                }
            }
        });
        mTeacherGuideFragment = TeacherGuideFragment.newInstance();
        mProgrammeList = new LiveProgrammeList(getActivity(), mDimBackground);
        mTeacherCommand.setOnTeacherHeadClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTeacherInfoDialog();
            }
        });
    }

    private ViewTreeObserver.OnGlobalLayoutListener onGlobalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {

        @Override
        public void onGlobalLayout() {

            Rect rect = new Rect();
            //获取到程序显示的区域，包括标题栏，但不包括状态
            getActivity().getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
            //获取屏幕的高度
            int screenHeight = getActivity().getWindow().getDecorView().getRootView().getHeight();
            //此处就是用来获取键盘的高度的， 在键盘没有弹出的时候 此高度为0 键盘弹出的时候为一个正数
            int heightDifference = screenHeight - rect.bottom;
            if (heightDifference > 0) {
                mKeyBoardIsOpen = true;
            }
            if (heightDifference == 0 && mSpeakEditText.isShown() && mKeyBoardIsOpen) {
                mSpeakLayout.setVisibility(View.GONE);
                mLiveSpeak.setVisibility(View.VISIBLE);
                mVideoContainer.setVisibility(View.VISIBLE);
                if (mSpeakEditText != null) {
                    mSpeakEditText.setText("");
                }
                mKeyBoardIsOpen = false;
            }
        }
    };

    @Override
    public void onPause() {
        super.onPause();
        if (mInputMethodManager != null && mSpeakEditText != null) {
            mInputMethodManager.hideSoftInputFromWindow(mSpeakEditText.getWindowToken(), 0);
        }
    }

    @OnClick({R.id.speakEditText, R.id.sendSpeak, R.id.liveSpeak})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.speakEditText:
                break;
            case R.id.sendSpeak:
                if (mInputMethodManager != null && mSpeakEditText != null) {
                    mInputMethodManager.hideSoftInputFromWindow(mSpeakEditText.getWindowToken(), 0);
                }
                if (mSpeakEditText != null && !TextUtils.isEmpty(mSpeakEditText.getText().toString())) {
                    NettyClient.getInstance().sendMessage(mSpeakEditText.getText().toString());
                    mSpeakEditText.setText("");
                }
//                mSpeakLayout.setVisibility(View.GONE);
                mLiveSpeak.setVisibility(View.VISIBLE);
                mVideoContainer.setVisibility(View.VISIBLE);
                break;
            case R.id.liveSpeak:
                if (LocalUser.getUser().isLogin()) {
                    if (mLiveMessage != null &&
                            mLiveMessage.getTeacher() != null &&
                            mLiveMessage.getTeacher().getTeacherAccountId() != 0) {
                        sendLiveSpeak();
                    } else {
                        ToastUtil.curt(R.string.live_time_is_not);
                    }
                } else {
                    Launcher.with(getActivity(), SignInActivity.class).executeForResult(REQUEST_CODE_LOGIN);
                }
                break;
        }
    }

    private void sendLiveSpeak() {
        mLiveSpeak.setVisibility(View.GONE);
        mVideoContainer.setVisibility(View.GONE);
        mSpeakLayout.setVisibility(View.VISIBLE);


        mSpeakEditText.setFocusable(true);
        mSpeakEditText.setFocusableInTouchMode(true);
        mSpeakEditText.requestFocus();
        if (mSpeakEditText.isShown()) {
            boolean b = mInputMethodManager.isActive(mSpeakEditText);
            if (b) {
                mInputMethodManager.showSoftInput(mSpeakEditText, InputMethodManager.SHOW_FORCED);
            }
        }
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
            mTeacherCommand.setTeacherCommand(getString(R.string.live_teacher_order, chatData.getMsg()));
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        disconnectNettySocket();
        View content = getActivity().findViewById(android.R.id.content);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            content.getViewTreeObserver().removeOnGlobalLayoutListener(onGlobalLayoutListener);
        } else {
            content.getViewTreeObserver().removeGlobalOnLayoutListener(onGlobalLayoutListener);
        }
    }

    @Override
    public void onBackPressed() {
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            if (mLivePlayer != null) {
                mLivePlayer.fullScreen(false);
            }
        } else if (mSpeakLayout.isShown() && mVideoContainer.getVisibility() == View.GONE) {
            mVideoContainer.setVisibility(View.VISIBLE);
            mSpeakLayout.setVisibility(View.GONE);
            mLiveSpeak.setVisibility(View.VISIBLE);
        } else {
            super.onBackPressed();
        }
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
        API.Live.getLiveMessage().setTag(TAG)
                .setCallback(new Callback2<Resp<LiveMessage>, LiveMessage>() {
                    @Override
                    public void onRespSuccess(LiveMessage liveMessage) {
                        mLiveMessage = liveMessage;
                        if (mServerIpPort != null) {
                            connectNettySocket();
                        }

                        if (mLiveMessage.getTeacher() != null) { // 在直播
                            if (mLiveInteractionFragment != null) {
                                mLiveInteractionFragment.setTeacherInfo(mLiveMessage.getTeacher());
                            }
                            showLiveViews();
                        } else if (mLiveMessage.getNotice() != null) { // 未直播,显示通告
                            showNoLiveViews();
                        }

                        if (mLiveMessage.getProgram() != null) {
                            mProgrammeList.setProgramme(mLiveMessage.getProgram());
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
            // TODO: 2016/11/23 测试数据
//            mLivePlayer.setVideoPath(active.getRtmp());
            mLivePlayer.setVideoPath("rtmp://live.hkstv.hk.lxdns.com/live/hks");
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
        mSlidingTabLayout.setPadding(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10,
                getResources().getDisplayMetrics()));
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
        View customView = mTitleBar.getCustomView();
        LinearLayout liveProgramme = (LinearLayout) customView.findViewById(R.id.liveProgramme);
        liveProgramme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLiveProgramme();
            }
        });
    }

    private void showLiveProgramme() {
        if (mProgrammeList != null) {
            mProgrammeList.show(mTitleBar);
        }
    }

    private void showTeacherInfoDialog() {
        LiveMessage.TeacherInfo teacherInfo = mLiveMessage.getTeacher();
        if (teacherInfo != null) {
            LiveTeacherInfoFragment liveTeacherInfoFragment
                    = LiveTeacherInfoFragment.newInstance(teacherInfo);
            liveTeacherInfoFragment.show(getSupportFragmentManager());
        }
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_LOGIN && resultCode == RESULT_OK) {
            mLiveInteractionFragment.setLoginSuccess(true);
            disconnectNettySocket();
            if (mLiveMessage != null) {
                connectNettySocket();
            }
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            mTitleBar.setVisibility(View.GONE);
            mTeacherCommand.setVisibility(View.GONE);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT);
            mVideoContainer.setLayoutParams(params);
            mKeyBoardIsOpen = false;
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            mTitleBar.setVisibility(View.VISIBLE);
            mTeacherCommand.setVisibility(View.VISIBLE);
            int playerHeight = getResources().getDimensionPixelOffset(R.dimen.player_height);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    playerHeight);
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
                            int enterPageProductId = 1;
                            if (hasPositions) {
                                String varietyType = homePositions.getCashOpS().get(0).getVarietyType();
                                for (int i = 0; i < mProductPkgList.size(); i++) {
                                    if (varietyType.equalsIgnoreCase(mProductPkgList.get(i).getProduct().getVarietyType())) {
                                        enterPageProductId = i;
                                        break;
                                    }
                                }
                            } else {
                                for (int i = 0; i < mProductPkgList.size(); i++) {
                                    if (Product.US_CRUDE_ID == mProductPkgList.get(i).getProduct().getVarietyId()) {
                                        enterPageProductId = i;
                                        break;
                                    }
                                }
                            }
                            ProductPkg productPkg = mProductPkgList.get(enterPageProductId);
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

        private FragmentManager mFragmentManager;

        public LivePageFragmentAdapter(FragmentManager supportFragmentManager) {
            super(supportFragmentManager);
            this.mFragmentManager = supportFragmentManager;
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
                    return mTeacherGuideFragment;
            }
            return null;
        }

        @Override
        public int getCount() {
            return 2;
        }

    }
}
