package com.jnhyxx.html5.fragment.home;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.jnhyxx.html5.R;
import com.jnhyxx.html5.domain.Information;
import com.jnhyxx.html5.fragment.BaseFragment;
import com.jnhyxx.html5.net.API;
import com.jnhyxx.html5.net.Callback2;
import com.jnhyxx.html5.net.Resp;
import com.jnhyxx.html5.utils.ToastUtil;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static com.jnhyxx.html5.R.id.cardView;

/**
 * Created by ${wangJie} on 2017/2/16.
 * 交易攻略
 */

public class TradingStrategyFragment extends BaseFragment {

    @BindView(R.id.listView)
    ListView mListView;
    @BindView(R.id.empty)
    TextView mEmpty;
    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout mSwipeRefreshLayout;

    private Unbinder mBind;

    private TextView mFootView;

    private int mPageSize = 15;
    private int mOffset = 0;

    private TradingStrategyAdapter mTradingStrategyAdapter;

    String data[] = new String[]{"https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1487225572513&di=5f423edad09528ef9de4b949183215e9&imgtype=0&src=http%3A%2F%2Fi.zeze.com%2Fattachment%2Fforum%2F201605%2F06%2F214815xnd5dz5t58fndt85.jpg",
            "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1487225572513&di=e878ddb9cdef6b8de977eb68f3340d11&imgtype=0&src=http%3A%2F%2Fpic55.nipic.com%2Ffile%2F20141208%2F19462408_171130083000_2.jpg"
            , "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1487225572513&di=5c8783edc39b289184131ef1b70445ff&imgtype=0&src=http%3A%2F%2Fpic28.nipic.com%2F20130424%2F11588775_115415688157_2.jpg"};

    public static TradingStrategyFragment newInstance() {

//        Bundle args = new Bundle();
        TradingStrategyFragment fragment = new TradingStrategyFragment();
//        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.listview_emptyview, container, false);
        mBind = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mListView.setDivider(null);
        initSwipeRefreshLayout();
        requestInfoList();

    }

    private void initSwipeRefreshLayout() {
        mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(true);
            }
        });
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mOffset = 0;
                if (mTradingStrategyAdapter != null) {
                    mTradingStrategyAdapter.clear();
                }
                requestInfoList();
            }
        });
    }

    private void requestInfoList() {
        API.Message.findNewsList(Information.TYPE_TRADING_STRATEGY, mOffset, mPageSize)
                .setTag(TAG)
                .setCallback(new Callback2<Resp<List<Information>>, List<Information>>() {
                    @Override
                    public void onRespSuccess(List<Information> informationList) {
                        for (Information data : informationList) {
                            Log.d(TAG, "交易攻略 " + data.toString());
                        }
                        if(informationList.isEmpty()){
                            informationList.add(new Information(data[0]));
                            informationList.add(new Information(data[1]));
                            informationList.add(new Information(data[2]));
                        }
                        updateViewWithData(informationList);
                    }

                    @Override
                    public void onFailure(VolleyError volleyError) {
                        super.onFailure(volleyError);
                        stopRefreshAnimation();
                    }
                }).fire();
    }

    private void updateViewWithData(List<Information> informationList) {
        stopRefreshAnimation();
        if (informationList == null) {
            return;
        }
        handleListViewFootView(informationList);
        if (mTradingStrategyAdapter == null) {
            mTradingStrategyAdapter = new TradingStrategyAdapter(getActivity());
            mListView.setAdapter(mTradingStrategyAdapter);
        }
        mTradingStrategyAdapter.addAll(informationList);
        mTradingStrategyAdapter.notifyDataSetInvalidated();
    }

    private void handleListViewFootView(List<Information> informationList) {
        if (mFootView == null) {
            mFootView = new TextView(getActivity());
            int padding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16, getResources().getDisplayMetrics());
            mFootView.setPadding(padding, padding, padding, padding);
            mFootView.setGravity(Gravity.CENTER);
            mFootView.setText(R.string.click_to_load_more);
            mFootView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mSwipeRefreshLayout.isRefreshing()) return;
                    mOffset += mPageSize;
                    requestInfoList();
                }
            });
            mListView.addFooterView(mFootView);
        }
        if (informationList.size() < mPageSize) {
            mListView.removeFooterView(mFootView);
            mFootView = null;
        }
    }

    private void stopRefreshAnimation() {
        if (mSwipeRefreshLayout.isRefreshing()) {
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }

    static class TradingStrategyAdapter extends ArrayAdapter<Information> {

        Context mContext;

        public TradingStrategyAdapter(Context context) {
            super(context, 0);
            mContext = context;
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.row_trading_strategy, null);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.bindDataWithView(getItem(position), mContext);
            return convertView;
        }

        static class ViewHolder {
            @BindView(R.id.strategyTitle)
            TextView mStrategyTitle;
            @BindView(R.id.image)
            ImageView mImage;
            @BindView(cardView)
            CardView mCardView;

            ViewHolder(View view) {
                ButterKnife.bind(this, view);
            }

            public void bindDataWithView(Information item, Context context) {
                if (!TextUtils.isEmpty(item.getCover())) {
                    Picasso.with(context).load(item.getCover()).into(mImage);
                }
            }

            @OnClick({R.id.strategyTitle, R.id.image})
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.strategyTitle:
                        ToastUtil.curt("交易攻略详情");
                        break;
                    case R.id.image:
                        ToastUtil.curt("交易攻略详情");
                        break;
                }
            }
        }
    }
}
