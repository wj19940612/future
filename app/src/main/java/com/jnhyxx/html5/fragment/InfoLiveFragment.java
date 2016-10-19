package com.jnhyxx.html5.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.jnhyxx.html5.R;
import com.jnhyxx.html5.net.API;
import com.jnhyxx.html5.net.Callback1;
import com.jnhyxx.html5.net.Resp;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by ${wangJie} on 2016/10/18.
 */

public class InfoLiveFragment extends BaseFragment {

    @BindView(R.id.listView)
    ListView mListView;
    @BindView(R.id.emptyView)
    TextView mEmptyView;
    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.infoLive)
    Button mInfoLive;

    private Unbinder mBind;

    private boolean isLoad;


    public static InfoLiveFragment newInstance() {
        InfoLiveFragment mInfoLiveFragment = new InfoLiveFragment();
        return mInfoLiveFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_info_list, container, false);
        mBind = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mInfoLive.setVisibility(View.VISIBLE);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBind.unbind();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        if (isVisibleToUser && isAdded() && !getActivity().isFinishing() && !isLoad) {
            getInfoLiveData();
        }
        super.setUserVisibleHint(isVisibleToUser);
    }

    private void getInfoLiveData() {
        API.Message.findNewsByUrl(API.getInfoLiveUrl())
                .setTag(TAG)
                .setIndeterminate(this)
                .setCallback(new Callback1<Resp<JsonObject>>() {
                                 @Override
                                 protected void onRespSuccess(Resp<JsonObject> resp) {
                                     Log.d(TAG, "直播数据" + resp.getData());
                                 }
                             }
                ).fire();
    }

    @OnClick(R.id.infoLive)
    public void onClick() {
        getInfoLiveData();
    }
}
