package com.jnhyxx.html5.activity.account;

import android.os.Bundle;
import android.view.View;

import com.jnhyxx.html5.R;
import com.jnhyxx.html5.activity.BaseActivity;
import com.jnhyxx.html5.domain.BankcardAuth;
import com.jnhyxx.html5.domain.NameAuth;
import com.jnhyxx.html5.domain.ProfileSummary;
import com.jnhyxx.html5.domain.local.User;
import com.jnhyxx.html5.net.API;
import com.jnhyxx.html5.net.Resp;
import com.jnhyxx.html5.view.IconTextRow;
import com.johnz.kutils.Launcher;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ProfileActivity extends BaseActivity {

    @BindView(R.id.nameAuth)
    IconTextRow mNameAuth;
    @BindView(R.id.bankcard)
    IconTextRow mBankcard;
    @BindView(R.id.phoneAuth)
    IconTextRow mPhoneAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ButterKnife.bind(this);

        API.Account.getProfileSummary(User.getUser().getLoginInfo().getTokenInfo().getToken())
                .setIndeterminate(this).setTag(TAG)
                .setCallback(new Resp.Callback<ProfileSummary>() {
                    @Override
                    protected void onRespSuccess(ProfileSummary profileSummary) {
                        updateProfileView(profileSummary);
                    }
                }).post();
    }

    private void updateProfileView(ProfileSummary profileSummary) {
        if (profileSummary.getUserStatus() == NameAuth.STATUS_NOT_FILLED) {
            mNameAuth.setSubText(R.string.unauthorized);
        } else if (profileSummary.getUserStatus() == NameAuth.STATUS_FILLED) {
            mNameAuth.setSubText(R.string.filled);
        } else if (profileSummary.getUserStatus() == NameAuth.STATUS_BE_BOUND) {
            mNameAuth.setSubText(R.string.authorized);
        }

        if (profileSummary.getBankStatus() == BankcardAuth.STATUS_NOT_FILLED) {
            mBankcard.setSubText(R.string.unauthorized);
        } else if (profileSummary.getBankStatus() == BankcardAuth.STATUS_FILLED) {
            mBankcard.setSubText(R.string.filled);
        } else if (profileSummary.getBankStatus() == BankcardAuth.STATUS_BE_BOUND) {
            mBankcard.setSubText(R.string.authorized);
        }

        mPhoneAuth.setSubText(profileSummary.getTele());
    }

    @OnClick({R.id.nameAuth, R.id.bankcard})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.nameAuth:
                Launcher.with(this, NameAuthActivity.class).execute();
                break;
            case R.id.bankcard:
                Launcher.with(this, BankcardAuthActivity.class).execute();
                break;
        }
    }
}
