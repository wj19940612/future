package com.jnhyxx.html5.fragment;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jnhyxx.html5.R;
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

    @BindView(R.id.balance)
    TextView mBalance;
    @BindView(R.id.score)
    TextView mScore;
    @BindView(R.id.signInButton)
    TextView mSignIn;
    @BindView(R.id.signUp)
    TextView mSignUp;
    @BindView(R.id.messageCenter)
    IconTextRow mMessageCenter;
    @BindView(R.id.fundDetail)
    IconTextRow mFundDetail;
    @BindView(R.id.scoreDetail)
    IconTextRow mScoreDetail;
    @BindView(R.id.personalInfo)
    IconTextRow mPersonalInfo;
    @BindView(R.id.paidToPromote)
    IconTextRow mPaidToPromote;
    @BindView(R.id.nickname)
    TextView mNickname;
    @BindView(R.id.signArea)
    LinearLayout mSignArea;
    @BindView(R.id.recharge)
    TextView mRecharge;
    @BindView(R.id.withdraw)
    TextView mWithdraw;
    @BindView(R.id.fundArea)
    LinearLayout mFundArea;
    @BindView(R.id.titleBar)
    TitleBar mTitleBar;

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
            mNickname.setText(getString(R.string.nickname_logged,
                    User.getUser().getLoginInfo().getUserInfo().getNick()));
            mSignArea.setVisibility(View.GONE);
            mFundArea.setVisibility(View.VISIBLE);
            mTitleBar.setRightVisible(true);
            mTitleBar.setOnRightViewClickListener(new View.OnClickListener() {
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
            mTitleBar.setRightVisible(false);

            mBalance.setText(R.string.zero);
            mScore.setText(R.string.zero);
        }
    }

    private void requestFundInfo() {
        API.Finance.getFundInfo(User.getUser().getToken()).setTag(TAG)
                .setCallback(new Callback2<Resp<FundInfo>, FundInfo>() {
                    @Override
                    public void onRespSuccess(FundInfo fundInfo) {
                        mBalance.setText(FinanceUtil.formatWithScale(fundInfo.getUsedAmt()));
                        mScore.setText(FinanceUtil.formatWithScale(fundInfo.getScore()));
                    }
                }).fire();
    }

    @OnClick({R.id.signInButton, R.id.signUp, R.id.recharge, R.id.withdraw, R.id.messageCenter, R.id.fundDetail, R.id.scoreDetail, R.id.personalInfo, R.id.paidToPromote})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.signInButton:
                Launcher.with(getActivity(), SignInActivity.class).execute();
                break;
            case R.id.signUp:
                Launcher.with(getActivity(), SignUpActivity.class).execute();
                break;
            case R.id.recharge:
                API.Account.getBankcardInfo(User.getUser().getToken()).setTag(TAG)
                        .setCallback(new Callback2<Resp<BankcardAuth>, BankcardAuth>() {
                            @Override
                            public void onRespSuccess(BankcardAuth bankcardAuth) {
                                Launcher.with(getActivity(), RechargeActivity.class)
                                        .putExtra(Launcher.EX_PAYLOAD, bankcardAuth)
                                        .execute();
                            }
                        }).fire();
                break;
            case R.id.withdraw:
                API.Account.getBankcardInfo(User.getUser().getToken()).setTag(TAG)
                        .setCallback(new Callback2<Resp<BankcardAuth>, BankcardAuth>() {
                            @Override
                            public void onRespSuccess(BankcardAuth bankcardAuth) {
                                Launcher.with(getActivity(), WithdrawActivity.class)
                                        .putExtra(Launcher.EX_PAYLOAD, bankcardAuth)
                                        .execute();
                            }
                        }).fire();
                break;
            case R.id.messageCenter:
                Launcher.with(getActivity(), MessageCenterActivity.class).execute();
                break;
            case R.id.fundDetail:
                openFundDetailPage(true);
                break;
            case R.id.scoreDetail:
                openFundDetailPage(false);
                break;
            case R.id.personalInfo:
                API.Account.getProfileSummary(User.getUser().getToken()).setTag(TAG)
                        .setCallback(new Callback2<Resp<ProfileSummary>, ProfileSummary>() {
                            @Override
                            public void onRespSuccess(ProfileSummary profileSummary) {
                                Launcher.with(getActivity(), ProfileActivity.class)
                                        .putExtra(Launcher.EX_PAYLOAD, profileSummary)
                                        .execute();
                            }
                        }).fire();
                break;
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
                    }).fire();
        } else {
            Launcher.with(getActivity(), SignInActivity.class).execute();
        }
    }
}
