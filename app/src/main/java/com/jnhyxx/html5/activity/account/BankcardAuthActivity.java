package com.jnhyxx.html5.activity.account;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jnhyxx.html5.R;
import com.jnhyxx.html5.activity.BaseActivity;
import com.jnhyxx.html5.domain.BankcardAuth;
import com.jnhyxx.html5.domain.NameAuth;
import com.jnhyxx.html5.domain.model.LocalCacheUserInfoManager;
import com.jnhyxx.html5.domain.model.UserInfo;
import com.jnhyxx.html5.fragment.BankListFragment;
import com.jnhyxx.html5.net.API;
import com.jnhyxx.html5.net.Callback;
import com.jnhyxx.html5.net.Callback2;
import com.jnhyxx.html5.net.Resp;
import com.jnhyxx.html5.utils.ToastUtil;
import com.jnhyxx.html5.utils.ValidationWatcher;
import com.jnhyxx.html5.view.dialog.SmartDialog;
import com.johnz.kutils.Launcher;
import com.johnz.kutils.ViewUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class BankcardAuthActivity extends BaseActivity implements BankListFragment.OnBankItemClickListener {

    public static final String NAME_AUTH_RESULT = "nameAuthResult";

    @BindView(R.id.cardholderName)
    EditText mCardholderName;
    @BindView(R.id.bankcardNum)
    EditText mBankcardNum;
    @BindView(R.id.phoneNum)
    EditText mPhoneNum;
    @BindView(R.id.submitToAuthButton)
    TextView mSubmitToAuthButton;
    @BindView(R.id.payingBank)
    TextView mPayingBank;
    @BindView(R.id.bankcardInputArea)
    LinearLayout mBankcardInputArea;
    @BindView(R.id.bank)
    TextView mBank;
    @BindView(R.id.hiddenBankcardNum)
    TextView mHiddenBankcardNum;
    @BindView(R.id.unbindBankcard)
    Button mUnbindBankcard;
    @BindView(R.id.bankcardImageArea)
    LinearLayout mBankcardImageArea;
    @BindView(R.id.fragmentContainer)
    FrameLayout mFragmentContainer;

    NameAuth.Result mNameAuthResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bankcard_auth);
        ButterKnife.bind(this);

        mCardholderName.addTextChangedListener(mValidationWatcher);
        mBankcardNum.addTextChangedListener(mValidationWatcher);
        mPayingBank.addTextChangedListener(mValidationWatcher);
        mPhoneNum.addTextChangedListener(mValidationWatcher);

//        updateBankcardView(getIntent());

//        API.User.getUserNameAuth(com.jnhyxx.html5.domain.local.User.getUser().getToken())
//                .setTag(TAG)
//                .setCallback(new Callback2<Resp<NameAuth>, NameAuth>() {
//                    @Override
//                    public void onRespSuccess(NameAuth nameAuth) {
//                        if (nameAuth.getStatus() == NameAuth.STATUS_NOT_FILLED) {
//                            showAuthNameDialog(nameAuth);
//                        } else {
//                            mCardholderName.setText(nameAuth.getUserName());
//                        }
//                    }
//                }).fire();
        LocalCacheUserInfoManager mLocalCacheUserInfoManager = LocalCacheUserInfoManager.getInstance();
        if (!mLocalCacheUserInfoManager.isLogin()) {
            ToastUtil.curt(R.string.nickname_unknown);
        }
        UserInfo user = mLocalCacheUserInfoManager.getUser();
        if (user != null) {
            if (user.getIdStatus() == 0) {
                showAuthNameDialog();
            }
        }
    }
//
//    private void updateBankcardView(Intent intent) {
//        BankcardAuth bankcardAuth = (BankcardAuth) intent.getSerializableExtra(Launcher.EX_PAYLOAD);
//        if (bankcardAuth.getStatus() == BankcardAuth.STATUS_BE_BOUND) {
//            mBankcardInputArea.setVisibility(View.GONE);
//            mBankcardImageArea.setVisibility(View.VISIBLE);
//        } else if (bankcardAuth.getStatus() == BankcardAuth.STATUS_FILLED) {
//            mBankcardNum.setText(bankcardAuth.getBankNum());
//            mPayingBank.setText(bankcardAuth.getBankName());
//            mPhoneNum.setText(bankcardAuth.getPhone());
//        }
//    }

    //    private void showAuthNameDialog(final NameAuth nameAuth) {
    private void showAuthNameDialog() {
        SmartDialog.with(getActivity(), R.string.dialog_unauthorized_name)
                .setPositive(R.string.go_and_auth, new SmartDialog.OnClickListener() {
                    @Override
                    public void onClick(Dialog dialog) {
                        Launcher.with(getActivity(), NameAuthActivity.class);
                        // TODO: 2016/9/9 原来的逻辑
//                                .putExtra(Launcher.EX_PAYLOAD, nameAuth)
//                                .executeForResult(REQUEST_CODE);
                    }
                })
                .setNegative(R.string.cancel, new SmartDialog.OnClickListener() {
                    @Override
                    public void onClick(Dialog dialog) {
                        finish();
                    }
                })
                .setCancelableOnTouchOutside(false)
                .show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            mNameAuthResult = (NameAuth.Result) data.getSerializableExtra(NAME_AUTH_RESULT);
            mCardholderName.setText(mNameAuthResult.getRealName());
            SmartDialog.dismiss(this);
        }
    }

    private ValidationWatcher mValidationWatcher = new ValidationWatcher() {
        @Override
        public void afterTextChanged(Editable editable) {
            boolean enable = checkSubmitButtonEnable();
            if (enable != mSubmitToAuthButton.isEnabled()) {
                mSubmitToAuthButton.setEnabled(enable);
            }
        }
    };

    private boolean checkSubmitButtonEnable() {
        String cardholderName = ViewUtil.getTextTrim(mCardholderName);
        String bankcardNum = ViewUtil.getTextTrim(mBankcardNum);
        String payingBank = ViewUtil.getTextTrim(mPayingBank);
        String phoneNum = ViewUtil.getTextTrim(mPhoneNum);
        if (TextUtils.isEmpty(cardholderName) || TextUtils.isEmpty(bankcardNum)
                || TextUtils.isEmpty(payingBank) || TextUtils.isEmpty(phoneNum)) {
            return false;
        }
        return true;
    }

    @OnClick({R.id.payingBank, R.id.submitToAuthButton, R.id.unbindBankcard})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.payingBank:
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragmentContainer, new BankListFragment(), BankListFragment.BANK_LIST).commit();
                break;
            case R.id.submitToAuthButton:
                String bankcardNum = ViewUtil.getTextTrim(mBankcardNum);
                String payingBank = ViewUtil.getTextTrim(mPayingBank);
                String phoneNum = ViewUtil.getTextTrim(mPhoneNum);
                API.User.updateBankcard(com.jnhyxx.html5.domain.local.User.getUser().getToken(), bankcardNum, payingBank, phoneNum)
                        .setIndeterminate(this).setTag(TAG)
                        .setCallback(new Callback<Resp<BankcardAuth>>() {
                            @Override
                            public void onReceive(final Resp<BankcardAuth> resp) {
                                if (resp.isSuccess()) {

                                    SmartDialog.with(getActivity(), resp.getMsg())
                                            .setCancelableOnTouchOutside(false)
                                            .setPositive(R.string.ok, new SmartDialog.OnClickListener() {
                                                @Override
                                                public void onClick(Dialog dialog) {
                                                    dialog.dismiss();
                                                    BankcardAuth auth = resp.getData();
                                                    auth.setStatus(BankcardAuth.STATUS_FILLED);
                                                    setResultForCalling(auth);
                                                    finish();
                                                }
                                            }).show();

                                } else {
                                    SmartDialog.with(getActivity(), resp.getMsg()).show();
                                }
                            }
                        }).fire();
                break;
            case R.id.unbindBankcard:
                SmartDialog.with(getActivity(), R.string.dialog_please_contact_services_to_unbind).show();
                break;
        }
    }

    /**
     * 由提现页面唤起,回传 BankcardAuth
     * <p>
     * 由个人信息页面唤起,回传 BankcardAuth NameAuth
     * <p>
     * 由充值页面唤起,回传 BankcardAuth
     *
     * @param data
     */
    private void setResultForCalling(BankcardAuth data) {
        if (getCallingActivity() == null) return;
        String fromClass = getCallingActivity().getClassName();

        if (fromClass.equals(WithdrawActivity.class.getName())) {
            Intent intent = new Intent().putExtra(WithdrawActivity.RESULT_BANKCARD_AUTH, data);
            setResult(RESULT_OK, intent);
        }

        if (fromClass.equals(ProfileActivity.class.getName())) {
            Intent intent = new Intent()
                    .putExtra(ProfileActivity.RESULT_BANKCARD_AUTH, data)
                    .putExtra(ProfileActivity.RESULT_NAME_AUTH, mNameAuthResult);
            setResult(RESULT_OK, intent);
        }

        if (fromClass.equals(RechargeActivity.class.getName())) {
            Intent intent = new Intent().putExtra(RechargeActivity.RESULT_BANKCARD_AUTH, data);
            setResult(RESULT_OK, intent);
        }
    }

    @Override
    public void onBankItemClick(BankListFragment.Bank bank) {
        mPayingBank.setText(bank.name);
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(BankListFragment.BANK_LIST);
        getSupportFragmentManager().beginTransaction().remove(fragment).commit();
    }
}

