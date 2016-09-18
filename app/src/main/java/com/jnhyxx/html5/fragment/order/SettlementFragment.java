package com.jnhyxx.html5.fragment.order;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.jnhyxx.html5.R;
import com.jnhyxx.html5.activity.order.OrderDetailActivity;
import com.jnhyxx.html5.domain.market.Product;
import com.jnhyxx.html5.domain.order.OrderDetail;
import com.jnhyxx.html5.domain.order.SettlementOrder;
import com.jnhyxx.html5.fragment.BaseFragment;
import com.jnhyxx.html5.net.API;
import com.jnhyxx.html5.net.Callback2;
import com.jnhyxx.html5.net.Resp;
import com.johnz.kutils.DateUtil;
import com.johnz.kutils.FinanceUtil;
import com.johnz.kutils.Launcher;
import com.johnz.kutils.StrUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class SettlementFragment extends BaseFragment {

    @BindView(android.R.id.list)
    ListView mList;
    @BindView(android.R.id.empty)
    TextView mEmpty;
    private Unbinder mBinder;

    private Product mProduct;
    private int mFundType;
    private int mPageNo;
    private int mPageSize;

    public static SettlementFragment newInstance(Product product, int fundType) {
        SettlementFragment fragment = new SettlementFragment();
        Bundle args = new Bundle();
        args.putSerializable(Product.EX_PRODUCT, product);
        args.putInt(Product.EX_FUND_TYPE, fundType);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mProduct = (Product) getArguments().getSerializable(Product.EX_PRODUCT);
            mFundType = getArguments().getInt(Product.EX_FUND_TYPE);
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
        mList.setEmptyView(mEmpty);
        mList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                final SettlementOrder settlementOrder = (SettlementOrder) adapterView.getItemAtPosition(position);
                if (settlementOrder != null) {
                    API.Order.getOrderDetail(settlementOrder.getShowId(), mFundType).setTag(TAG)
                            .setCallback(new Callback2<Resp<OrderDetail>, OrderDetail>() {
                                @Override
                                public void onRespSuccess(OrderDetail orderDetail) {
                                    Launcher.with(getActivity(), OrderDetailActivity.class)
                                            .putExtra(Launcher.EX_PAYLOAD, settlementOrder)
                                            .putExtra(Launcher.EX_PAYLOAD_1, orderDetail)
                                            .putExtra(Product.EX_PRODUCT, mProduct)
                                            .execute();
                                }
                            }).fire();
                }
            }
        });

        API.Order.getSettlementOrderList(mProduct.getContractsCode(), mFundType, mPageNo, mPageSize)
                .setCallback(new Callback2<Resp<List<SettlementOrder>>, List<SettlementOrder>>() {
                    @Override
                    public void onRespSuccess(List<SettlementOrder> settlementOrders) {
                        updateSettlementOrderListView(settlementOrders);
                    }
                }).setTag(TAG).fire();
    }

    private void updateSettlementOrderListView(List<SettlementOrder> settlementOrders) {
        if (settlementOrders == null) return;

        SettlementAdapter adapter = new SettlementAdapter(getContext(), mProduct);
        adapter.addAll(settlementOrders);
        mList.setAdapter(adapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBinder.unbind();
    }

    static class SettlementAdapter extends ArrayAdapter<SettlementOrder> {

        private Product mProduct;

        public SettlementAdapter(Context context, Product product) {
            super(context, 0);
            mProduct = product;
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
            viewHolder.bindingData(getItem(position), getContext(), mProduct);
            return convertView;
        }

        static class ViewHolder {
            @BindView(R.id.sellDate)
            TextView mSellDate;
            @BindView(R.id.tradeType)
            TextView mTradeType;
            @BindView(R.id.sellType)
            TextView mSellType;
            @BindView(R.id.lossProfit)
            TextView mLossProfit;

            ViewHolder(View view) {
                ButterKnife.bind(this, view);
            }

            public void bindingData(SettlementOrder item, Context context, Product product) {
                String sellTime;
                if (DateUtil.isInThisYear(item.getSellTime())) {
                    sellTime = DateUtil.format(item.getSellTime(), "MM/dd hh:mm");
                } else {
                    sellTime = DateUtil.format(item.getSellTime(), "yyyy/MM/dd hh:mm:ss");
                }
                String[] saleDates = sellTime.split(" ");
                if (saleDates.length == 2) {
                    mSellDate.setText(StrUtil.mergeTextWithRatio(saleDates[0], "\n" + saleDates[1], 1.5f));
                }

                int tradeType = item.getDirection();
                if (tradeType == SettlementOrder.DIRECTION_LONG) {
                    mTradeType.setText(R.string.bullish);
                    mTradeType.setBackgroundResource(R.drawable.bg_red_primary);
                } else {
                    mTradeType.setText(R.string.bearish);
                    mTradeType.setBackgroundResource(R.drawable.bg_green_primary);
                }

                int sellType = item.getUnwindType();
                if (sellType == SettlementOrder.SELL_OUT_SYSTEM_CLEAR) {
                    mSellType.setText(R.string.time_up_sale);
                } else if (sellType == SettlementOrder.SELL_OUT_STOP_LOSS) {
                    mSellType.setText(R.string.stop_loss_sale);
                } else if (sellType == SettlementOrder.SELL_OUT_STOP_PROFIT) {
                    mSellType.setText(R.string.stop_profit_sale);
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
                    String lossProfitInner = "(" + FinanceUtil.formatWithScale(FinanceUtil.multiply(lossProfit, rate).doubleValue()) + ")";
                    mLossProfit.setTextColor(color);
                    mLossProfit.setText(StrUtil.mergeTextWithRatioColor(lossProfitForeign, "\n" + lossProfitInner, 0.5f, grayColor));

                } else {
                    double lossProfit = item.getWinOrLoss();
                    int color;
                    String lossProfitInner;
                    if (lossProfit < 0) {
                        color = ContextCompat.getColor(context, R.color.greenPrimary);
                        lossProfitInner = FinanceUtil.formatWithScale(lossProfit, product.getLossProfitScale())
                                + product.getCurrencyUnit();

                    } else {
                        color = ContextCompat.getColor(context, R.color.redPrimary);
                        lossProfitInner = "+" + FinanceUtil.formatWithScale(lossProfit, product.getLossProfitScale())
                                + product.getCurrencyUnit();
                    }
                    mLossProfit.setTextColor(color);
                    mLossProfit.setText(lossProfitInner);
                }
            }
        }
    }
}
