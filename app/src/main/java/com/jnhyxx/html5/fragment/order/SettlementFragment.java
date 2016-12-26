package com.jnhyxx.html5.fragment.order;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.jnhyxx.html5.R;
import com.jnhyxx.html5.activity.order.OrderDetailActivity;
import com.jnhyxx.html5.constans.Unit;
import com.jnhyxx.html5.domain.market.Product;
import com.jnhyxx.html5.domain.order.OrderDetail;
import com.jnhyxx.html5.domain.order.SettledOrder;
import com.jnhyxx.html5.domain.order.SettledOrderSet;
import com.jnhyxx.html5.fragment.BaseFragment;
import com.jnhyxx.html5.net.API;
import com.jnhyxx.html5.net.Callback;
import com.jnhyxx.html5.net.Callback2;
import com.jnhyxx.html5.net.Resp;
import com.jnhyxx.html5.utils.UmengCountEventIdUtils;
import com.johnz.kutils.DateUtil;
import com.johnz.kutils.FinanceUtil;
import com.johnz.kutils.Launcher;
import com.johnz.kutils.StrUtil;
import com.umeng.analytics.MobclickAgent;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class SettlementFragment extends BaseFragment {

    @BindView(android.R.id.list)
    ListView mList;
    @BindView(android.R.id.empty)
    TextView mEmpty;
    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout mSwipeRefreshLayout;

    private Unbinder mBinder;

    private Product mProduct;
    private int mFundType;
    private String mFundUnit;
    private int mPageNo;
    private int mPageSize;
    private Set<String> mSet;
    private boolean mHoldingFragmentClosedPositions;

    private TextView mFooter;
    private SettlementAdapter mSettlementAdapter;

    public static SettlementFragment newInstance(Product product, int fundType) {
        SettlementFragment fragment = new SettlementFragment();
        Bundle args = new Bundle();
        args.putParcelable(Product.EX_PRODUCT, product);
        args.putInt(Product.EX_FUND_TYPE, fundType);
        fragment.setArguments(args);
        return fragment;
    }

    public void setHoldingFragmentClosedPositions(boolean closePositions) {
        mHoldingFragmentClosedPositions = closePositions;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mProduct = getArguments().getParcelable(Product.EX_PRODUCT);
            mFundType = getArguments().getInt(Product.EX_FUND_TYPE);
            mFundUnit = (mFundType == Product.FUND_TYPE_CASH ? Unit.YUAN : Unit.GOLD);
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && isAdded()) {
            if (mHoldingFragmentClosedPositions) {
                mPageNo = 1;
                mSet.clear();
                mSwipeRefreshLayout.setRefreshing(true);
                requestSettlementOrderList();
                mHoldingFragmentClosedPositions = false;
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settlement, container, false);
        mBinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mPageNo = 1;
        mPageSize = 10;
        mSet = new HashSet<>();

        mList.setEmptyView(mEmpty);
        mList.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                int topRowVerticalPosition =
                        (mList == null || mList.getChildCount() == 0) ? 0 : mList.getChildAt(0).getTop();
                mSwipeRefreshLayout.setEnabled(firstVisibleItem == 0 && topRowVerticalPosition >= 0);
            }
        });
        mList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                final SettledOrder settledOrder = (SettledOrder) adapterView.getItemAtPosition(position);
                if (settledOrder != null) {
                    MobclickAgent.onEvent(getActivity(), UmengCountEventIdUtils.ORDER_CLEANING_DETAILS);
                    API.Order.getOrderDetail(settledOrder.getShowId(), mFundType).setTag(TAG)
                            .setCallback(new Callback2<Resp<OrderDetail>, OrderDetail>() {
                                @Override
                                public void onRespSuccess(OrderDetail orderDetail) {
                                    Launcher.with(getActivity(), OrderDetailActivity.class)
                                            .putExtra(Launcher.EX_PAYLOAD, settledOrder)
                                            .putExtra(Launcher.EX_PAYLOAD_1, orderDetail)
                                            .putExtra(Product.EX_PRODUCT, mProduct)
                                            .putExtra(Product.EX_FUND_TYPE, mFundType)
                                            .execute();
                                }
                            }).fire();
                }
            }
        });

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPageNo = 1;
                mSet.clear();
                requestSettlementOrderList();
            }
        });

        requestSettlementOrderList();
    }

    private void requestSettlementOrderList() {
        API.Order.getSettlementOrderList(mProduct.getVarietyId(), mFundType, mPageNo, mPageSize)
                .setCallback(new Callback<Resp<SettledOrderSet>>() {
                    @Override
                    public void onReceive(Resp<SettledOrderSet> settledOrderSetResp) {
                        if (settledOrderSetResp.isSuccess()) {
                            SettledOrderSet settledOrderSet = settledOrderSetResp.getData();
                            updateSettlementOrderListView(settledOrderSet.getData());
                        } else {
                            if (mSwipeRefreshLayout.isRefreshing()) {
                                mSwipeRefreshLayout.setRefreshing(false);
                            }
                        }
                    }

                    @Override
                    public void onFailure(VolleyError volleyError) {
                        super.onFailure(volleyError);
                        if (mSwipeRefreshLayout.isRefreshing()) {
                            mSwipeRefreshLayout.setRefreshing(false);
                        }
                    }
                }).setTag(TAG).fire();
    }

    private void updateSettlementOrderListView(List<SettledOrder> settlementOrderList) {
        if (settlementOrderList == null) {
            if (mSwipeRefreshLayout.isRefreshing()) {
                mSwipeRefreshLayout.setRefreshing(false);
            }
            return;
        }

        if (mFooter == null) {
            mFooter = new TextView(getActivity());
            int padding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16,
                    getResources().getDisplayMetrics());
            mFooter.setPadding(padding, padding, padding, padding);
            mFooter.setGravity(Gravity.CENTER);
            mFooter.setText(R.string.click_to_load_more);
            mFooter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mSwipeRefreshLayout.isRefreshing()) return;

                    mPageNo++;
                    requestSettlementOrderList();
                }
            });
            mList.addFooterView(mFooter);
        }

        if (settlementOrderList.size() < mPageSize) {
            // When get number of data is less than mPageSize, means no data anymore
            // so remove footer
            mList.removeFooterView(mFooter);
        }

        if (mSwipeRefreshLayout.isRefreshing()) {
            mSettlementAdapter.clear();
            mSwipeRefreshLayout.setRefreshing(false);
        }

        if (mSettlementAdapter == null) {
            mSettlementAdapter = new SettlementAdapter(getContext(), mProduct, mFundUnit);
            mList.setAdapter(mSettlementAdapter);
        }
        for (SettledOrder item : settlementOrderList) {
            if (mSet.add(item.getShowId())) {
                mSettlementAdapter.add(item);
            }
        }

        mSettlementAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBinder.unbind();
    }

    static class SettlementAdapter extends ArrayAdapter<SettledOrder> {

        private Product mProduct;
        private String mFundUnit;

        public SettlementAdapter(Context context, Product product, String fundUnit) {
            super(context, 0);
            mProduct = product;
            mFundUnit = fundUnit;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_settlement, null);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.bindingData(getItem(position), getContext(),
                    mProduct, mFundUnit, position);
            return convertView;
        }

        static class ViewHolder {
            @BindView(R.id.splitBlock)
            View mSplitBlock;
            @BindView(R.id.sellYearMonthDay)
            TextView mSellYearMonthDay;
            @BindView(R.id.sellHourMin)
            TextView mSellHourMin;
            @BindView(R.id.tradeType)
            TextView mTradeType;
            @BindView(R.id.sellType)
            TextView mSellType;
            @BindView(R.id.lossProfit)
            TextView mLossProfit;


            ViewHolder(View view) {
                ButterKnife.bind(this, view);
            }

            public void bindingData(SettledOrder item, Context context,
                                    Product product, String fundUnit, int position) {
                if (position == 0) {
                    mSplitBlock.setVisibility(View.VISIBLE);
                } else {
                    mSplitBlock.setVisibility(View.GONE);
                }

                String sellTime;
                if (DateUtil.isInThisYear(item.getSellTime())) {
                    sellTime = DateUtil.format(item.getSellTime(), "MM/dd HH:mm");
                } else {
                    sellTime = DateUtil.format(item.getSellTime(), "yyyy/MM/dd HH:mm");
                }
                String[] saleDates = sellTime.split(" ");
                if (saleDates.length == 2) {
                    mSellYearMonthDay.setText(saleDates[0]);
                    mSellHourMin.setText(saleDates[1]);
                }

                int tradeType = item.getDirection();
                if (tradeType == SettledOrder.DIRECTION_LONG) {
                    mTradeType.setText(R.string.buy_long);
                    mTradeType.setBackgroundResource(R.drawable.bg_red_primary);
                } else {
                    mTradeType.setText(R.string.sell_short);
                    mTradeType.setBackgroundResource(R.drawable.bg_green_primary);
                }

                int sellType = item.getUnwindType();
                if (sellType == SettledOrder.SELL_OUT_SYSTEM_CLEAR) {
                    mSellType.setText(R.string.time_up_sale);
                } else if (sellType == SettledOrder.SELL_OUT_STOP_LOSS) {
                    mSellType.setText(R.string.stop_loss_sale);
                } else if (sellType == SettledOrder.SELL_OUT_STOP_PROFIT) {
                    mSellType.setText(R.string.stop_profit_sale);
                } else if (sellType == SettledOrder.SELL_OUT_MANUALLY) {
                    mSellType.setText(R.string.manually_sale);
                } else {
                    mSellType.setText(R.string.market_price_sale);
                }

                if (product.isForeign()) {
                    double lossProfit = item.getWinOrLoss();
                    double rate = item.getRatio();
                    int grayColor = Color.parseColor("#666666"); // gray
                    int color;
                    String lossProfitForeign;
                    if (lossProfit < 0) {
                        color = ContextCompat.getColor(context, R.color.greenPrimary);
                        lossProfitForeign = FinanceUtil.formatWithScale(lossProfit, product.getLossProfitScale())
                                + product.getCurrencyUnit();

                    } else {
                        color = ContextCompat.getColor(context, R.color.redPrimary);
                        lossProfitForeign = "+" + FinanceUtil.formatWithScale(lossProfit, product.getLossProfitScale())
                                + product.getCurrencyUnit();
                    }
                    String lossProfitRmb = "(" + FinanceUtil.formatWithScale(Math.abs(FinanceUtil.multiply(lossProfit, rate).doubleValue()))
                            + fundUnit +  ")";
                    mLossProfit.setTextColor(color);
                    mLossProfit.setText(StrUtil.mergeTextWithRatioColor(lossProfitForeign, "\n" + lossProfitRmb, 0.7f, grayColor));
                } else {
                    double lossProfit = item.getWinOrLoss();
                    int color;
                    String lossProfitRmb;
                    if (lossProfit < 0) {
                        color = ContextCompat.getColor(context, R.color.greenPrimary);
                        lossProfitRmb = FinanceUtil.formatWithScale(lossProfit, product.getLossProfitScale())
                                + product.getCurrencyUnit();

                    } else {
                        color = ContextCompat.getColor(context, R.color.redPrimary);
                        lossProfitRmb = "+" + FinanceUtil.formatWithScale(lossProfit, product.getLossProfitScale())
                                + product.getCurrencyUnit();
                    }
                    mLossProfit.setTextColor(color);
                    mLossProfit.setText(lossProfitRmb);
                }
            }
        }
    }
}
