package com.jnhyxx.html5.fragment.home;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.jnhyxx.html5.R;
import com.jnhyxx.html5.domain.order.ProfitRankModel;
import com.jnhyxx.html5.fragment.BaseFragment;
import com.jnhyxx.html5.net.API;
import com.jnhyxx.html5.net.Callback2;
import com.jnhyxx.html5.net.Resp;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by ${wangJie} on 2017/2/14.
 * 首页排行榜fragment
 */

public class YesterdayProfitRankFragment extends BaseFragment {

    @BindView(R.id.listView)
    ListView mListView;
    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(android.R.id.empty)
    TextView mEmpty;

    private Unbinder mBind;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ranklist, container, false);
        mBind = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mListView.setEmptyView(mEmpty);

        getYesterdayProfitRank();
    }

    /**
     * 获取昨日盈利榜数据
     */
    private void getYesterdayProfitRank() {
        API.Order.getProfitRank()
                .setTag(TAG)
                .setIndeterminate(this)
                .setCallback(new Callback2<Resp<List<ProfitRankModel>>, List<ProfitRankModel>>() {

                    @Override
                    public void onRespSuccess(List<ProfitRankModel> profitRankModels) {

                        for (ProfitRankModel data : profitRankModels) {
                            Log.d(TAG, "盈利  " + data.toString());
                        }
                    }
                })
                .fire();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBind.unbind();
    }
}
