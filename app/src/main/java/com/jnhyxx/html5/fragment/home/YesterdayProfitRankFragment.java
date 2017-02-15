package com.jnhyxx.html5.fragment.home;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
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

                        for (int i = 0; i < 10; i++) {
                            new ProfitRankModel(10000+i,);
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

    static class ProfitRankAdapter extends ArrayAdapter<ProfitRankModel> {

        Context mContext;

        public ProfitRankAdapter(Context context) {
            super(context, 0);
            mContext = context;
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.row_profit_rank, null);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);

            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.bindDataWithView(mContext, getItem(position), position);
            return convertView;

        }

        static class ViewHolder {
            @BindView(R.id.ranking)
            TextView mRanking;
            @BindView(R.id.phoneNum)
            TextView mPhoneNum;
            @BindView(R.id.profit)
            TextView mProfit;

            ViewHolder(View view) {
                ButterKnife.bind(this, view);
            }

            public void bindDataWithView(Context context, ProfitRankModel item, int position) {
                mRanking.setText(String.valueOf(position + 1));

            }
        }
    }
}
