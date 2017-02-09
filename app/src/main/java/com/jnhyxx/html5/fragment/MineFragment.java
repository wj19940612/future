package com.jnhyxx.html5.fragment;


import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.jnhyxx.html5.R;
import com.jnhyxx.html5.activity.BaseActivity;
import com.jnhyxx.html5.activity.account.AboutUsActivity;
import com.jnhyxx.html5.activity.account.BankcardBindingActivity;
import com.jnhyxx.html5.activity.account.IdeaFeedbackActivity;
import com.jnhyxx.html5.activity.account.MessageCenterActivity;
import com.jnhyxx.html5.activity.account.RechargeActivity;
import com.jnhyxx.html5.activity.account.SignInActivity;
import com.jnhyxx.html5.activity.account.SignUpActivity;
import com.jnhyxx.html5.activity.account.TradeDetailActivity;
import com.jnhyxx.html5.activity.account.UserInfoActivity;
import com.jnhyxx.html5.activity.account.WithdrawActivity;
import com.jnhyxx.html5.activity.web.PaidToPromoteActivity;
import com.jnhyxx.html5.domain.account.UserFundInfo;
import com.jnhyxx.html5.domain.account.UserInfo;
import com.jnhyxx.html5.domain.local.LocalUser;
import com.jnhyxx.html5.net.API;
import com.jnhyxx.html5.net.Callback;
import com.jnhyxx.html5.net.Callback1;
import com.jnhyxx.html5.net.Resp;
import com.jnhyxx.html5.utils.FontUtil;
import com.jnhyxx.html5.utils.ToastUtil;
import com.jnhyxx.html5.utils.UmengCountEventIdUtils;
import com.jnhyxx.html5.utils.transform.CircleTransform;
import com.jnhyxx.html5.view.CircularAnnulusImageView;
import com.jnhyxx.html5.view.IconTextRow;
import com.jnhyxx.html5.view.TitleBar;
import com.jnhyxx.html5.view.dialog.SmartDialog;
import com.johnz.kutils.FinanceUtil;
import com.johnz.kutils.Launcher;
import com.johnz.kutils.net.CookieManger;
import com.squareup.picasso.Picasso;
import com.umeng.analytics.MobclickAgent;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static com.jnhyxx.html5.R.id.paidToPromote;


public class MineFragment extends BaseFragment {

    private static final int REQ_CODE_ADD_BANK = 407;

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
    @BindView(R.id.headImage)
    CircularAnnulusImageView mHeadImage;
    @BindView(R.id.feedback)
    IconTextRow mFeedback;

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
        FontUtil.setTt0173MFont(mBalance);

        mTitleBar.setOnRightViewClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MobclickAgent.onEvent(getActivity(), UmengCountEventIdUtils.SET);
                Launcher.with(getActivity(), UserInfoActivity.class).execute();
            }
        });
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && isAdded()) {
            requestUserInfo();
        }
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
        requestUserInfo();
    }

    private void requestUserInfo() {
        updateUsableMoneyScore(new LocalUser.Callback() {
            @Override
            public void onUpdateCompleted() {
                updateAccountInfoView();

            }
        });
    }

    private void updateAccountInfoView() {
        if (LocalUser.getUser().isLogin()) {
            mSignArea.setVisibility(View.GONE);
            mFundArea.setVisibility(View.VISIBLE);
            mTitleBar.setRightVisible(true);

            UserInfo userInfo = LocalUser.getUser().getUserInfo();

            String userName = userInfo.getUserName();
            double moneyUsable = userInfo.getMoneyUsable();
            double scoreUsable = userInfo.getScoreUsable();
            mNickname.setText(getString(R.string.nickname_logged, userName));
            mBalance.setText(FinanceUtil.formatWithScale(moneyUsable));
            mScore.setText(getString(R.string.mine_score, FinanceUtil.formatWithScale(scoreUsable)));
            if (!TextUtils.isEmpty(userInfo.getUserPortrait())) {
                Picasso.with(getActivity()).load(userInfo.getUserPortrait()).error(R.drawable.ic_user_info_head_visitor).transform(new CircleTransform()).into(mHeadImage);
            } else {
                if (!TextUtils.isEmpty(userInfo.getChinaSex())) {
                    if (userInfo.isUserisBoy()) {
                        mHeadImage.setImageResource(R.drawable.ic_user_info_head_boy);
                    } else {
                        mHeadImage.setImageResource(R.drawable.ic_user_info_head_girl);
                    }
                } else {
                    mHeadImage.setImageResource(R.drawable.ic_user_info_head_visitor);
                }
            }
        } else {
            mSignArea.setVisibility(View.VISIBLE);
            mFundArea.setVisibility(View.GONE);
            mNickname.setText(R.string.no_logged);
            mTitleBar.setRightVisible(false);
            mBalance.setText(R.string.zero);
            mScore.setText(getString(R.string.mine_score, getString(R.string.zero)));
            mHeadImage.setImageResource(R.drawable.ic_user_info_head_visitor);
        }
    }

    @OnClick({R.id.signInButton, R.id.signUpButton, R.id.recharge, R.id.withdraw, R.id.messageCenter, R.id.tradeDetail, R.id.aboutUs, paidToPromote, R.id.headImage, R.id.feedback})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.signInButton:
                MobclickAgent.onEvent(getActivity(), UmengCountEventIdUtils.LOGIN);
                Launcher.with(getActivity(), SignInActivity.class).executeForResult(BaseActivity.REQ_CODE_LOGIN);
                break;
            case R.id.signUpButton:
                MobclickAgent.onEvent(getActivity(), UmengCountEventIdUtils.REGISTER);
                Launcher.with(getActivity(), SignUpActivity.class).execute();
                break;
            case R.id.recharge: //充值
                MobclickAgent.onEvent(getActivity(), UmengCountEventIdUtils.RECHARGE);
                Launcher.with(getActivity(), RechargeActivity.class).execute();
                break;
            case R.id.withdraw: //提现
                openWithdrawPage();
                break;
            case R.id.messageCenter:
                MobclickAgent.onEvent(getActivity(), UmengCountEventIdUtils.MESSAGE_CENTER);
                Launcher.with(getActivity(), MessageCenterActivity.class).execute();
                break;
            case R.id.tradeDetail:
                openTradeDetailPage();
                break;
            case R.id.aboutUs:
                MobclickAgent.onEvent(getActivity(), UmengCountEventIdUtils.ABOUT_US);
                Launcher.with(getActivity(), AboutUsActivity.class).execute();
                break;
            case paidToPromote:
                openPaidToPromotePage();
                break;
            case R.id.headImage:
                MobclickAgent.onEvent(getActivity(), UmengCountEventIdUtils.USER_HEAD);
                if (LocalUser.getUser().isLogin()) {
                    Launcher.with(getActivity(), UserInfoActivity.class).execute();
                } else {
                    Launcher.with(getActivity(), SignInActivity.class).execute();
                }
                break;
            case R.id.feedback:
                MobclickAgent.onEvent(getActivity(), UmengCountEventIdUtils.FEED_BACK);
                Launcher.with(getActivity(), IdeaFeedbackActivity.class).execute();
                break;
        }
    }

    private void openWithdrawPage() {
        MobclickAgent.onEvent(getActivity(), UmengCountEventIdUtils.WITHDRAW);
        if (LocalUser.getUser().isBankcardFilled()) {
            Launcher.with(getActivity(), WithdrawActivity.class).execute();
        } else {
            showBindBankCardDialog();
        }
    }

    private void showBindBankCardDialog() {
        SpannableStringBuilder confirmSpannableString = new SpannableStringBuilder();
        confirmSpannableString.append(getString(R.string.go_to_bind_bank_card));
        ForegroundColorSpan confirmColorSpan = new ForegroundColorSpan(ContextCompat.getColor(getActivity(), R.color.blueAssist));

        SmartDialog.with(getActivity(), R.string.You_have_not_binding_bank_cards)
                .setPositive(R.string.go_to_bind_bank_card, new SmartDialog.OnClickListener() {
                    @Override
                    public void onClick(Dialog dialog) {
                        getActivity().startActivityForResult(new Intent(getActivity(), BankcardBindingActivity.class),REQ_CODE_ADD_BANK );
                        dialog.dismiss();
                    }
                }).setNegative(R.string.cancel)
                .show();
    }

    private void openPaidToPromotePage() {
        MobclickAgent.onEvent(getActivity(), UmengCountEventIdUtils.EXPAND_EARN_MONEY);
        if (LocalUser.getUser().isLogin()) {
            API.User.getPromoteCode().setTag(TAG).setIndeterminate(this)
                    .setCallback(new Callback<Resp<JsonObject>>() {
                        @Override
                        public void onReceive(Resp<JsonObject> resp) {
                            if (resp.isSuccess()) {
                                Launcher.with(getActivity(), PaidToPromoteActivity.class)
                                        .putExtra(PaidToPromoteActivity.EX_URL, API.getPromotePage())
                                        .putExtra(PaidToPromoteActivity.EX_TITLE, getString(R.string.paid_to_promote))
                                        .putExtra(PaidToPromoteActivity.EX_RAW_COOKIE, CookieManger.getInstance().getRawCookie())
                                        .execute();
                            } else if (resp.getCode() == Resp.CODE_GET_PROMOTE_CODE_FAILED) {
                                showAskApplyPromoterDialog();
                            } else {
                                ToastUtil.show(resp.getMsg());
                            }
                        }
                    }).fire();
        } else {
            Launcher.with(getActivity(), SignInActivity.class).execute();
        }
    }

    private void showAskApplyPromoterDialog() {
        SmartDialog.with(getActivity(), R.string.dialog_you_are_not_promoter_yet)
                .setPositive(R.string.ok, new SmartDialog.OnClickListener() {
                    @Override
                    public void onClick(Dialog dialog) {
                        dialog.dismiss();
                        applyForPromoter();
                    }
                })
                .setNegative(R.string.cancel)
                .show();
    }

    private void applyForPromoter() {
        API.User.becomePromoter().setTag(TAG)
                .setCallback(new Callback1<Resp<JsonObject>>() {
                    @Override
                    protected void onRespSuccess(Resp<JsonObject> resp) {
                        if (resp.isSuccess()) {
                            ToastUtil.show(resp.getMsg());
                            Launcher.with(getActivity(), PaidToPromoteActivity.class)
                                    .putExtra(PaidToPromoteActivity.EX_URL, API.getPromotePage())
                                    .putExtra(PaidToPromoteActivity.EX_TITLE, getString(R.string.paid_to_promote))
                                    .putExtra(PaidToPromoteActivity.EX_RAW_COOKIE, CookieManger.getInstance().getRawCookie())
                                    .execute();
                        }
                    }
                }).fire();
    }

    private void openTradeDetailPage() {
        if (LocalUser.getUser().isLogin()) {
            API.Finance.getFundInfo().setTag(TAG)
                    .setIndeterminate(this)
                    .setCallback(new Callback1<Resp<UserFundInfo>>() {
                        @Override
                        protected void onRespSuccess(Resp<UserFundInfo> resp) {
                            UserFundInfo userFundInfo = resp.getData();
                            Launcher.with(getActivity(), TradeDetailActivity.class)
                                    .putExtra(Launcher.EX_PAYLOAD, userFundInfo).execute();
                        }
                    }).fire();
        } else {
            Launcher.with(getActivity(), SignInActivity.class).execute();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_CODE_ADD_BANK && resultCode == AppCompatActivity.RESULT_OK) {
            openWithdrawPage();
        }
    }
}
