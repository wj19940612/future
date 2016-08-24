package com.jnhyxx.html5.fragment;


import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jnhyxx.html5.R;
import com.jnhyxx.html5.activity.account.AboutUsActivity;
import com.jnhyxx.html5.activity.account.FundDetailActivity;
import com.jnhyxx.html5.activity.account.MessageCenterActivity;
import com.jnhyxx.html5.activity.account.ProfileActivity;
import com.jnhyxx.html5.activity.account.RechargeActivity;
import com.jnhyxx.html5.activity.account.SignInActivity;
import com.jnhyxx.html5.activity.account.SignUpActivity;
import com.jnhyxx.html5.activity.account.WithdrawActivity;
import com.jnhyxx.html5.domain.BankcardAuth;
import com.jnhyxx.html5.domain.ProfileSummary;
import com.jnhyxx.html5.domain.finance.FundInfo;
import com.jnhyxx.html5.domain.local.User;
import com.jnhyxx.html5.net.API;
import com.jnhyxx.html5.net.Callback2;
import com.jnhyxx.html5.net.Resp;
import com.jnhyxx.html5.view.IconTextRow;
import com.jnhyxx.html5.view.TitleBar;
import com.johnz.kutils.FinanceUtil;
import com.johnz.kutils.Launcher;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class AccountFragment extends BaseFragment {
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
    @BindView(R.id.signUp)
    TextView mSignUp;
    //消息中心的未读消息数
    @BindView(R.id.account_tv_message_number)
    TextView mMessageNumber;
    //消息中心
    @BindView(R.id.messageCenter)
//    IconTextRow mMessageCenter;
            RelativeLayout mMessageCenter;
    //交易明细
    @BindView(R.id.fundDetail)
//    IconTextRow mFundDetail;
            RelativeLayout mFundDetail;

    //积分明细
//    @BindView(R.id.scoreDetail)
//    IconTextRow mScoreDetail;
    @BindView(R.id.personalInfo)
//    IconTextRow mPersonalInfo;
            RelativeLayout mPersonalInfo;
    @BindView(R.id.paidToPromote)
//    IconTextRow mPaidToPromote;
            RelativeLayout mPaidToPromote;
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
    //    @BindView(R.id.titleBar)
//    TitleBar mTitleBar;
    @BindView(R.id.fragment_account_titleBar_iv_setting)
    ImageView mSettingImageView;
    private Unbinder mBinder;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account, container, false);
        mBinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBinder.unbind();
    }

    @Override
    public void onResume() {
        super.onResume();
        updateAccountInfoView();
    }

    private void updateAccountInfoView() {
        if (User.getUser().isLogin()) {
            String format = String.format(" ", User.getUser().getLoginInfo().getUserInfo().getNick());
            Log.d(TAG, " " + format);
            mNickname.setText(getString(R.string.nickname_logged, User.getUser().getLoginInfo().getUserInfo().getNick()));
            mSignArea.setVisibility(View.GONE);
            mFundArea.setVisibility(View.VISIBLE);
           /* mTitleBar.setRightVisible(true);
            mTitleBar.setOnRightViewClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    User.getUser().logout();
                    updateAccountInfoView();
                }
            });*/
            mSettingImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    User.getUser().logout();
                    updateAccountInfoView();
                }
            });
            requestFundInfo();

        } else {
            mSignArea.setVisibility(View.VISIBLE);
            mFundArea.setVisibility(View.GONE);
            mNickname.setText(R.string.nickname_unknown);
//            mTitleBar.setRightVisible(true);

            mBalance.setText(R.string.zero);
            mScore.setText(getString(R.string.account_mine_integral, getString(R.string.zero)));
        }
    }

    private void requestFundInfo() {
        API.Finance.getFundInfo(User.getUser().getToken()).setTag(TAG)
                .setCallback(new Callback2<Resp<FundInfo>, FundInfo>() {
                    @Override
                    public void onRespSuccess(FundInfo fundInfo) {
                        mBalance.setText(FinanceUtil.formatWithScale(fundInfo.getUsedAmt()));
                        mScore.setText(getString(R.string.account_mine_integral, FinanceUtil.formatWithScale(fundInfo.getScore())));
                    }
                }).post();

    }

    @OnClick({R.id.signInButton, R.id.signUp, R.id.recharge, R.id.withdraw, R.id.messageCenter, R.id.fundDetail, R.id.personalInfo, R.id.paidToPromote})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.signInButton:
                Launcher.with(getActivity(), SignInActivity.class).execute();
                break;
            //zhuce
            case R.id.signUp:
                Launcher.with(getActivity(), SignUpActivity.class).execute();
                break;
            //充值
            case R.id.recharge:
                API.Account.getBankcardInfo(User.getUser().getToken()).setTag(TAG)
                        .setCallback(new Callback2<Resp<BankcardAuth>, BankcardAuth>() {
                            @Override
                            public void onRespSuccess(BankcardAuth bankcardAuth) {
                                Launcher.with(getActivity(), RechargeActivity.class)
                                        .putExtra(Launcher.EX_PAYLOAD, bankcardAuth)
                                        .execute();
                            }
                        }).post();
                break;
            //提现
            case R.id.withdraw:
                API.Account.getBankcardInfo(User.getUser().getToken()).setTag(TAG)
                        .setCallback(new Callback2<Resp<BankcardAuth>, BankcardAuth>() {
                            @Override
                            public void onRespSuccess(BankcardAuth bankcardAuth) {
                                Launcher.with(getActivity(), WithdrawActivity.class)
                                        .putExtra(Launcher.EX_PAYLOAD, bankcardAuth)
                                        .execute();
                            }
                        }).post();
                break;
            case R.id.messageCenter:
                Launcher.with(getActivity(), MessageCenterActivity.class).execute();
                break;
            case R.id.fundDetail:
                openFundDetailPage(true);
                break;
            //积分明细
//            case R.id.scoreDetail:
//                openFundDetailPage(false);
//                break;
            //关于我们
            case R.id.personalInfo:
//                API.Account.getProfileSummary(User.getUser().getToken()).setTag(TAG)
//                        .setCallback(new Callback2<Resp<ProfileSummary>, ProfileSummary>() {
//                            @Override
//                            public void onRespSuccess(ProfileSummary profileSummary) {
//                                Launcher.with(getActivity(), ProfileActivity.class)
//                                        .putExtra(Launcher.EX_PAYLOAD, profileSummary)
//                                        .execute();
//                            }
//                        }).post();
//                break;
                Launcher.with(getActivity(), AboutUsActivity.class).execute();
            case R.id.paidToPromote:
                break;
        }
    }

    private void openFundDetailPage(final boolean isCash) {
        if (User.getUser().isLogin()) {
            API.Finance.getFundInfo(User.getUser().getToken()).setTag(TAG)
                    .setCallback(new Callback2<Resp<FundInfo>, FundInfo>() {
                        @Override
                        public void onRespSuccess(FundInfo fundInfo) {
                            Launcher.with(getActivity(), FundDetailActivity.class)
                                    .putExtra(Launcher.EX_PAYLOAD, fundInfo)
                                    .putExtra(FundDetailActivity.EX_IS_CASH, isCash)
                                    .execute();
                        }
                    }).post();
        } else {
            Launcher.with(getActivity(), SignInActivity.class).execute();
        }
    }
}
