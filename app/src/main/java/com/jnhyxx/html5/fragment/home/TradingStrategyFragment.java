package com.jnhyxx.html5.fragment.home;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.jnhyxx.html5.R;
import com.jnhyxx.html5.fragment.BaseFragment;
import com.jnhyxx.html5.utils.ToastUtil;
import com.squareup.picasso.Picasso;

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
        mListView.setBackgroundResource(android.R.color.white);
        initSwipeRefreshLayout();
        String data[] = new String[]{"https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1487225572513&di=5f423edad09528ef9de4b949183215e9&imgtype=0&src=http%3A%2F%2Fi.zeze.com%2Fattachment%2Fforum%2F201605%2F06%2F214815xnd5dz5t58fndt85.jpg",
                "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1487225572513&di=e878ddb9cdef6b8de977eb68f3340d11&imgtype=0&src=http%3A%2F%2Fpic55.nipic.com%2Ffile%2F20141208%2F19462408_171130083000_2.jpg"
                , "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1487225572513&di=5c8783edc39b289184131ef1b70445ff&imgtype=0&src=http%3A%2F%2Fpic28.nipic.com%2F20130424%2F11588775_115415688157_2.jpg"};
        TradingStrategyAdapter tradingStrategyAdapter = new TradingStrategyAdapter(getActivity());
        tradingStrategyAdapter.addAll(data);
        mListView.setAdapter(tradingStrategyAdapter);
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

            }
        });
    }

    static class TradingStrategyAdapter extends ArrayAdapter<String> {

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

            public void bindDataWithView(String item, Context context) {
                if (!TextUtils.isEmpty(item)) {
                    Picasso.with(context).load(item).into(mImage);
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
