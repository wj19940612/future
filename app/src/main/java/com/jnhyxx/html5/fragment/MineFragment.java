package com.jnhyxx.html5.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jnhyxx.html5.R;
import com.jnhyxx.html5.activity.account.AboutUsActivity;
import com.jnhyxx.html5.activity.account.RechargeActivity;
import com.jnhyxx.html5.activity.account.SignInActivity;
import com.jnhyxx.html5.activity.account.SignUpActivity;
import com.jnhyxx.html5.activity.account.WithdrawActivity;
import com.jnhyxx.html5.activity.setting.SettingActivity;
import com.jnhyxx.html5.domain.BankcardAuth;
import com.jnhyxx.html5.domain.account.UserFundInfo;
import com.jnhyxx.html5.domain.account.UserInfo;
import com.jnhyxx.html5.domain.local.LocalUser;
import com.jnhyxx.html5.net.API;
import com.jnhyxx.html5.net.Callback2;
import com.jnhyxx.html5.net.Resp;
import com.jnhyxx.html5.utils.CommonMethodUtils;
import com.jnhyxx.html5.utils.ToastUtil;
import com.jnhyxx.html5.view.IconTextRow;
import com.jnhyxx.html5.view.TitleBar;
import com.johnz.kutils.FinanceUtil;
import com.johnz.kutils.Launcher;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static android.app.Activity.RESULT_OK;

public class MineFragment extends BaseFragment {
    //登陆的请求码
    private static final int REQUEST_CODE_LOGIN = 9670;
    //设置界面的请求码
    private static final int REQUEST_CODE_SETTING = 352;
    //注册的请求码
    private static final int REQUEST_CODE_REGISTER = 6260;
    //提现的请求码
    private static final int REQUEST_CODE_WITHDRAW = 6329;
    //充值的请求码
    private static final int REQUEST_CODE_RECHARGE = 560;

    //账户余额
    @BindView(R.id.balance)
    TextView mBalance;
    //我的积分
    @BindView(R.id.score)
    TextView mScore;
    //登陆
    @BindView(R.id.signInButton)
    TextView mSignIn;
    //注册
    @BindView(R.id.signUpButton)
    TextView mSignUp;
    // TODO: 2016/8/29 消息中心的消息数可以使用TitleBar.setSubText();
    //消息中心
    @BindView(R.id.messageCenter)
    IconTextRow mMessageCenter;
    //交易明细
    @BindView(R.id.tradeDetail)
    IconTextRow mTradeDetail;
    @BindView(R.id.aboutUs)
    IconTextRow mAboutUs;
    @BindView(R.id.paidToPromote)
    IconTextRow mPaidToPromote;
    @BindView(R.id.nickname)
    TextView mNickname;
    //登陆和注册按钮的父容器
    @BindView(R.id.signArea)
    LinearLayout mSignArea;
    @BindView(R.id.recharge)
    TextView mRecharge;
    @BindView(R.id.withdraw)
    TextView mWithdraw;
    //充值和提现按钮的父容器
    @BindView(R.id.fundArea)
    LinearLayout mFundArea;
    @BindView(R.id.titleBar)
    TitleBar mTitleBar;

    private Unbinder mBinder;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mine, container, false);
        mBinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // TODO: 2016/9/12 判断用户是否登陆，如果没有登陆，则设置不可打开
        if (LocalUser.getUser().isLogin()) {
            mTitleBar.setRightVisible(true);
        } else {
            mTitleBar.setRightVisible(false);
        }

        mTitleBar.setOnRightViewClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Launcher.with(getActivity(), SettingActivity.class).executeForResult(REQUEST_CODE_SETTING);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBinder.unbind();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        updateAccountInfoView();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void updateAccountInfoView() {
        if (LocalUser.getUser().isLogin()) {
            UserInfo userInfo = LocalUser.getUser().getUserInfo();
            upDateUserInfoView(userInfo);
//            requestFundInfo();

        } else {
            mSignArea.setVisibility(View.VISIBLE);
            mFundArea.setVisibility(View.GONE);
            mNickname.setText(R.string.nickname_unknown);
            mTitleBar.setRightVisible(true);

            mBalance.setText(R.string.zero);
            mScore.setText(getString(R.string.account_mine_integral, getString(R.string.zero)));
        }
    }

    private void requestFundInfo() {
        API.Finance.getFundInfo().setTag(TAG)
                .setCallback(new Callback2<Resp<UserFundInfo>, UserFundInfo>() {
                    @Override
                    public void onRespSuccess(UserFundInfo fundInfo) {
                        mBalance.setText(FinanceUtil.formatWithScale(fundInfo.getMoneyUsable()));
                        mScore.setText(getString(R.string.account_mine_integral, FinanceUtil.formatWithScale(fundInfo.getScoreUsable())));
                    }
                }).fire();

    }

    @OnClick({R.id.signInButton, R.id.signUpButton, R.id.recharge, R.id.withdraw, R.id.messageCenter, R.id.tradeDetail, R.id.aboutUs, R.id.paidToPromote})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.signInButton:
                Launcher.with(getActivity(), SignInActivity.class).executeForResult(REQUEST_CODE_LOGIN);
                break;
            case R.id.signUpButton:
                Launcher.with(getActivity(), SignUpActivity.class).executeForResult(REQUEST_CODE_REGISTER);
                break;
            //充值
            case R.id.recharge:
//                API.User.getBankcardInfo(com.jnhyxx.html5.domain.local.User.getUser().getToken()).setTag(TAG)
//                        .setCallback(new Callback2<Resp<BankcardAuth>, BankcardAuth>() {
//                            @Override
//                            public void onRespSuccess(BankcardAuth bankcardAuth) {
//                                Launcher.with(getActivity(), RechargeActivity.class)
//                                        .putExtra(Launcher.EX_PAYLOAD, bankcardAuth)
//                                        .execute();
//                            }
//                        }).fire();
                // TODO: 2016/9/11 这里目前缺少判断，应该判断银行卡绑定状态
                startRechargeActivity();
                break;
            //提现
            case R.id.withdraw:
                startWithDrawActivity();
                break;
            case R.id.messageCenter:
                // TODO: 2016/9/8 目前没有系统消息的接口
//                Launcher.with(getActivity(), MessageCenterActivity.class).execute();
                break;
            case R.id.tradeDetail:
                openTradeDetailPage(true);
                break;
            case R.id.aboutUs:
                Launcher.with(getActivity(), AboutUsActivity.class).execute();
                break;
            case R.id.paidToPromote:
                break;
        }
    }

    private void startRechargeActivity() {
        UserInfo userInfo = LocalUser.getUser().getUserInfo();
        if (CommonMethodUtils.isNameAuth(userInfo)) {
            if (CommonMethodUtils.isBankAuth(userInfo)) {
                Launcher.with(getActivity(), RechargeActivity.class).executeForResult(REQUEST_CODE_RECHARGE);
            } else {
                ToastUtil.curt("您还没有绑定银行卡，请先绑定银行卡后再提现");
            }
        } else {
            ToastUtil.curt("您没有实名认证，请先实名认证后再提现");
        }
    }

    private void startWithDrawActivity() {
        UserInfo userInfo = LocalUser.getUser().getUserInfo();
        //如果没有实名认证，先实名认证
        if (!CommonMethodUtils.isNameAuth(userInfo)) {
            ToastUtil.curt("您没有实名认证，请先实名认证后在提现");
        } else {
            Launcher.with(getActivity(), WithdrawActivity.class).executeForResult(REQUEST_CODE_WITHDRAW);
        }
    }

    /**
     * 打开交易明细界面
     *
     * @param isCash
     */
    private void openTradeDetailPage(final boolean isCash) {
//        if (User.getUser().isLogin()) {
        // TODO: 2016/8/30 原来的界面，资金明细和积分明细在一起的
        /*    API.Finance.getFundInfo(User.getUser().getToken()).setTag(TAG)
    private void openFundDetailPage(final boolean isCash) {
        if (com.jnhyxx.html5.domain.local.User.getUser().isLogin()) {
            API.Finance.getFundInfo(com.jnhyxx.html5.domain.local.User.getUser().getToken()).setTag(TAG)
                    .setCallback(new Callback2<Resp<FundInfo>, FundInfo>() {
                        @Override
                        public void onRespSuccess(FundInfo fundInfo) {
                            Launcher.with(getActivity(), FundDetailActivity.class)
                                    .putExtra(Launcher.EX_PAYLOAD, fundInfo)
                                    .putExtra(FundDetailActivity.EX_IS_CASH, isCash)
                                    .execute();
                        }
                    }).fire();*/
           /* API.Finance.getFundInfo(User.getUser().getToken()).setTag(TAG)
                    .setCallback(new Callback2<Resp<TradeDetail>, TradeDetail>() {
                        @Override
                        public void onRespSuccess(TradeDetail tradeDetail) {
                            Launcher.with(getActivity(), TradeDetailActivity.class)
                                    .putExtra(Launcher.EX_PAYLOAD, tradeDetail)
                                    .execute();
                        }
                    }).fire();*/
//        API.Finance.getFundInfo("money").setTag(TAG)
//                .setCallback(new Callback2<Resp<TradeDetail>, TradeDetail>() {
//                    @Override
//                    public void onRespSuccess(TradeDetail tradeDetail) {
//                        Launcher.with(getActivity(), TradeDetailActivity.class)
//                                .putExtra(Launcher.EX_PAYLOAD, tradeDetail)
//                                .execute();
//                    }
//                }).fire();
//        } else {
//            Launcher.with(getActivity(), SignInActivity.class).execute();
//        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UserInfo userInfo = LocalUser.getUser().getUserInfo();
        Log.d(TAG, "我的界面的用户信息" + userInfo.toString());
        if (requestCode == REQUEST_CODE_LOGIN && resultCode == RESULT_OK) {
            upDateUserInfoView(userInfo);
        } else if (requestCode == REQUEST_CODE_REGISTER && resultCode == RESULT_OK) {
            upDateUserInfoView(userInfo);
        } else if (requestCode == REQUEST_CODE_SETTING && resultCode == RESULT_OK) {
            updateAccountInfoView();
        }
    }

    private void upDateUserInfoView(UserInfo userInfo) {
        if (userInfo == null) return;
        String userName = userInfo.getUserName();
        double moneyUsable = userInfo.getMoneyUsable();
        int scoreUsable = userInfo.getScoreUsable();
        mNickname.setText(getString(R.string.nickname_logged, userName));
        mBalance.setText(FinanceUtil.formatWithScale(moneyUsable));
        mScore.setText(getString(R.string.account_mine_integral, scoreUsable + ""));
        mSignArea.setVisibility(View.GONE);
        mFundArea.setVisibility(View.VISIBLE);
        mTitleBar.setRightVisible(true);
    }
}
