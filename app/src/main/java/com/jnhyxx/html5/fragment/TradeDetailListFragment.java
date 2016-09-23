package com.jnhyxx.html5.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.jnhyxx.html5.R;
import com.jnhyxx.html5.domain.account.TradeDetail;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by Administrator on 2016/8/30.
 */

public class TradeDetailListFragment extends BaseFragment {


    @BindView(R.id.listTradeDetail)
    ListView mListView;
    //积分剩余
    @BindView(R.id.tvIntegerlRemainNumber)
    TextView tvIntegerlRemainNumber;
    //冻结保证金
    @BindView(R.id.tvTradeDetailMarginRemain)
    TextView tvTradeDetailMarginRemain;

    /**
     * bundle所传递数据TradeDetail的key
     */
    private static final String KEY_TRADEDETAIL = "tradeDetail";
    /**
     * bundle所传递的fragmentId，代表是哪一个fragment
     */
    private static final String FRAGMENT_ID = "fragmentItem";
    private TradeDetail tradeDetail;
    private Unbinder bind;
    /**
     * 代表是哪一个fragment
     */
    private int fragmentItem;


    public TradeDetailListFragment() {
    }

    public static TradeDetailListFragment newInstance(TradeDetail tradeDetail) {
        TradeDetailListFragment mTradeDetailListFragment = new TradeDetailListFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(KEY_TRADEDETAIL, tradeDetail);
        mTradeDetailListFragment.setArguments(bundle);
        return mTradeDetailListFragment;
    }

    public static TradeDetailListFragment newInstance(TradeDetail tradeDetail, int fragmentItem) {
        TradeDetailListFragment mTradeDetailListFragment = new TradeDetailListFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(KEY_TRADEDETAIL, tradeDetail);
        bundle.putInt(FRAGMENT_ID, fragmentItem);
        mTradeDetailListFragment.setArguments(bundle);
        return mTradeDetailListFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            tradeDetail = (TradeDetail) getArguments().getSerializable(KEY_TRADEDETAIL);
            fragmentItem = getArguments().getInt(FRAGMENT_ID, -1);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ButterKnife.bind(getActivity());
        View view = inflater.inflate(R.layout.fragment_trade_detail, container, false);
        bind = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        bind.unbind();
    }

    class TradeDetailAdapter extends ArrayAdapter<TradeDetail> {
        Context context;

        public TradeDetailAdapter(Context context) {
            super(context, 0);
            this.context = context;
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.item_trade_detail, null);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.bindingData(getItem(position), getContext());
            return convertView;
        }

        class ViewHolder {
            @BindView(R.id.tvTradeDetailYear)
            TextView timeYear;
            @BindView(R.id.tvTradeDetailHour)
            TextView timeHour;
            @BindView(R.id.tvDataType)
            TextView dataType;
            @BindView(R.id.tvTradeDetailType)
            TextView tradeDetail;
            @BindView(R.id.tradeDetailGrade)
            TextView tradeDetailMarginRemain;

            ViewHolder(View view) {
                ButterKnife.bind(this, view);
            }

            public void bindingData(TradeDetail item, Context context) {

            }
        }
    }
}