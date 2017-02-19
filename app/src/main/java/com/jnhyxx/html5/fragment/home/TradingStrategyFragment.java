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
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.jnhyxx.html5.R;
import com.jnhyxx.html5.activity.web.TradeAnalyzeDetailsActivity;
import com.jnhyxx.html5.domain.Information;
import com.jnhyxx.html5.fragment.BaseFragment;
import com.jnhyxx.html5.fragment.HomeFragment;
import com.jnhyxx.html5.net.API;
import com.jnhyxx.html5.net.Callback2;
import com.jnhyxx.html5.net.Resp;
import com.johnz.kutils.Launcher;
import com.squareup.picasso.Picasso;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.jnhyxx.html5.R.id.cardView;

/**
 * Created by ${wangJie} on 2017/2/16.
 * 交易攻略
 */

public class TradingStrategyFragment extends BaseFragment implements AdapterView.OnItemClickListener, AbsListView.OnScrollListener {

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
    private Set<String> mSet;

    private TradingStrategyAdapter mTradingStrategyAdapter;

    private HomeFragment.OnListViewHeightListener mOnListViewHeightListener;

    public void setOnListViewHeightListener(HomeFragment.OnListViewHeightListener onListViewHeightListener) {
        mOnListViewHeightListener = onListViewHeightListener;
    }

    public static TradingStrategyFragment newInstance() {
        TradingStrategyFragment fragment = new TradingStrategyFragment();
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
        mSet = new HashSet<>();
        mListView.setDivider(null);
        mListView.setOnItemClickListener(this);
        mListView.setOnScrollListener(this);
        mEmpty.setText(R.string.coming_soon);
        mListView.setEmptyView(mEmpty);
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
                        updateViewWithData(informationList);
                        stopRefreshAnimation();
                    }

                    @Override
                    public void onFailure(VolleyError volleyError) {
                        super.onFailure(volleyError);
                        stopRefreshAnimation();
                    }
                }).fire();
    }

    private void updateViewWithData(List<Information> informationList) {
        if (informationList == null) {
            return;
        }
        handleListViewFootView(informationList);
        if (mTradingStrategyAdapter == null) {
            mTradingStrategyAdapter = new TradingStrategyAdapter(getActivity());
            mListView.setAdapter(mTradingStrategyAdapter);
        }

        for (Information data : informationList) {
            if (mSet.add(data.getId())) {
                mTradingStrategyAdapter.add(data);
            }
        }
        mTradingStrategyAdapter.notifyDataSetInvalidated();
        ViewGroup.LayoutParams params = mListView.getLayoutParams();
        int heightBasedOnChildren1 = com.jnhyxx.html5.utils.ViewUtil.setListViewHeightBasedOnChildren1(mListView);
        params.height = heightBasedOnChildren1 + (mListView.getDividerHeight() * (mTradingStrategyAdapter.getCount() - 1));
        // listView.getDividerHeight()获取子项间分隔符占用的高度
        // params.height最后得到整个ListView完整显示需要的高度
        mListView.setLayoutParams(params);
        mOnListViewHeightListener.listViewHeight(heightBasedOnChildren1);
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

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Information information = (Information) parent.getItemAtPosition(position);
        Launcher.with(getActivity(), TradeAnalyzeDetailsActivity.class).putExtra(Launcher.EX_PAYLOAD, information).execute();
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        int topRowVerticalPosition =
                (mListView == null || mListView.getChildCount() == 0) ? 0 : mListView.getChildAt(0).getTop();
        mSwipeRefreshLayout.setEnabled(firstVisibleItem == 0 && topRowVerticalPosition >= 0);
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
        }
    }
}
