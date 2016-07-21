package com.jnhyxx.html5.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jnhyxx.html5.R;
import com.jnhyxx.html5.view.IconTextRow;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class AccountFragment extends Fragment {

    @BindView(R.id.balance)
    TextView mBalance;
    @BindView(R.id.score)
    TextView mScore;
    @BindView(R.id.signIn)
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

    @OnClick(R.id.signUp)
    public void signUp() {

    }

}
