package com.jnhyxx.html5.fragment;

import android.content.Context;
import android.support.v4.app.Fragment;

import com.jnhyxx.html5.activity.BaseActivity;
import com.jnhyxx.html5.net.API;
import com.johnz.kutils.net.ApiIndeterminate;

public class BaseFragment extends Fragment implements ApiIndeterminate {

    protected String TAG;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        TAG = this.getClass().getSimpleName();
    }

    @Override
    public void onShow(String tag) {
        if (getActivity() != null && getActivity() instanceof BaseActivity) {
            ((BaseActivity) getActivity()).onShow(tag);
        }
    }

    @Override
    public void onDismiss(String tag) {
        if (getActivity() != null && getActivity() instanceof BaseActivity) {
            ((BaseActivity) getActivity()).onDismiss(tag);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        API.cancel(TAG);
    }
}
