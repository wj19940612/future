package com.jnhyxx.html5.fragment.order;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.jnhyxx.html5.R;
import com.jnhyxx.html5.activity.WebViewActivity;
import com.jnhyxx.html5.fragment.BaseFragment;
import com.jnhyxx.html5.net.API;
import com.jnhyxx.html5.utils.BlurEngine;
import com.johnz.kutils.Launcher;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class AgreementFragment extends BaseFragment {

    private static final String TYPE = "longOrShort";

    @BindView(R.id.emptyClickArea)
    View mEmptyClickArea;
    @BindView(R.id.tradeCooperationAgreement)
    TextView mTradeCooperationAgreement;
    @BindView(R.id.riskNotices)
    TextView mRiskNotices;
    @BindView(R.id.readAndAgreeSign)
    TextView mReadAndAgreeSign;

    @OnClick({R.id.emptyClickArea, R.id.tradeCooperationAgreement, R.id.riskNotices, R.id.readAndAgreeSign})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.emptyClickArea:
                if (mCallback != null) {
                    mCallback.onAgreementFragmentEmptyAreaClick();
                }
                break;
            case R.id.tradeCooperationAgreement:
                Launcher.with(getActivity(), WebViewActivity.class)
                        .putExtra(WebViewActivity.EX_TITLE, getString(R.string.cooperation_agreement_title))
                        .putExtra(WebViewActivity.EX_URL, API.getCooperationAgreementUrl())
                        .execute();
                break;
            case R.id.riskNotices:
                Launcher.with(getActivity(), WebViewActivity.class)
                        .putExtra(WebViewActivity.EX_TITLE, getString(R.string.risk_notices_title))
                        .putExtra(WebViewActivity.EX_URL, API.getRiskNoticesUrl())
                        .execute();
                break;
            case R.id.readAndAgreeSign:
                if (mCallback != null) {
                    mCallback.onAgreementFragmentAgreeBtnClick(mLongOrShort);
                }
                break;
        }
    }

    public interface Callback {
        void onAgreementFragmentAgreeBtnClick(int longOrShort);
        void onAgreementFragmentEmptyAreaClick();
        void onAgreementFragmentShow();
        void onAgreementFragmentExited();
    }
    private Unbinder mBinder;
    private BlurEngine mBlurEngine;
    private Callback mCallback;
    private int mLongOrShort;

    public static Fragment newInstance(int longOrShort) {
        AgreementFragment fragment = new AgreementFragment();
        Bundle args = new Bundle();
        args.putInt(TYPE, longOrShort);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Callback) {
            mCallback = (Callback) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnBuyBtnClickListener");
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mLongOrShort = getArguments().getInt(TYPE, 0);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBlurEngine = new BlurEngine(container, R.color.blackHalfTransparent);
        View view = inflater.inflate(R.layout.fragment_agreement, container, false);
        mBinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        mBlurEngine.onResume();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBlurEngine.onDestroyView();
        mBinder.unbind();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (mCallback != null) {
            mCallback.onAgreementFragmentExited();
        }
        mCallback = null;
    }

    @Override
    public Animation onCreateAnimation(int transit, final boolean enter, int nextAnim) {
        Animation animation;

        if (enter) {
            animation = AnimationUtils.loadAnimation(getContext(), R.anim.slide_in_from_bottom);
            animation.setAnimationListener(new EnterAnimListener());
        } else {
            animation = AnimationUtils.loadAnimation(getContext(), R.anim.slide_out_to_bottom);
        }

        return animation;
    }

    private class EnterAnimListener implements Animation.AnimationListener {

        @Override
        public void onAnimationStart(Animation animation) {
            if (mCallback != null) {
                mCallback.onAgreementFragmentShow();
            }
        }

        @Override
        public void onAnimationEnd(Animation animation) {
        }

        @Override
        public void onAnimationRepeat(Animation animation) {
        }
    }
}
