package com.jnhyxx.html5.fragment.order;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jnhyxx.html5.R;
import com.jnhyxx.html5.domain.market.Product;
import com.jnhyxx.html5.domain.order.HoldingOrder;
import com.jnhyxx.html5.fragment.BaseFragment;
import com.jnhyxx.html5.utils.presenter.OrderPresenter;
import com.johnz.kutils.FinanceUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class HoldingFragment extends BaseFragment implements OrderPresenter.IHoldingOrderView {

    @BindView(android.R.id.list)
    ListView mList;
    @BindView(R.id.totalProfitAndUnit)
    TextView mTotalProfitAndUnit;
    @BindView(R.id.totalProfit)
    TextView mTotalProfit;
    @BindView(R.id.oneKeyClosePositionBtn)
    TextView mOneKeyClosePositionBtn;
    @BindView(android.R.id.empty)
    LinearLayout mEmpty;
    @BindView(R.id.lossProfitArea)
    RelativeLayout mLossProfitArea;
    @BindView(R.id.totalProfitRmb)
    TextView mTotalProfitRmb;

    private Unbinder mBinder;

    private Product mProduct;
    private int mFundType;
    private OrderPresenter mOrderPresenter;
    private HoldingOrderAdapter mHoldingOrderAdapter;

    public static HoldingFragment newInstance(Product product, int fundType) {
        HoldingFragment fragment = new HoldingFragment();
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
        View view = inflater.inflate(R.layout.fragment_holding, container, false);
        mBinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBinder.unbind();
    }

    @Override
    public void onResume() {
        super.onResume();
        mOrderPresenter.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mOrderPresenter.onPause();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mList.setEmptyView(mEmpty);
        mTotalProfitAndUnit.setText(getString(R.string.holding_position_total_profit_and_unit,
                mProduct.getCurrencyUnit()));

        mOrderPresenter = new OrderPresenter(this);
        mOrderPresenter.loadHoldingOrderList(mProduct.getVarietyId(), mFundType);
    }

    @Override
    public void onShowHoldingOrderList(List<HoldingOrder> holdingOrderList) {
        if (holdingOrderList != null) {
            if (mHoldingOrderAdapter == null) {
                mHoldingOrderAdapter = new HoldingOrderAdapter(getContext(), mProduct, holdingOrderList);
                mList.setAdapter(mHoldingOrderAdapter);
            } else {
                mHoldingOrderAdapter.setHoldingOrderList(holdingOrderList);
            }
        }
    }

    @Override
    public void onShowTotalProfit(boolean hasHoldingOrders, double totalProfit, double ratio) {
        if (hasHoldingOrders) {
            if (mLossProfitArea.getVisibility() == View.GONE) {
                mLossProfitArea.setVisibility(View.VISIBLE);
            }
            int scale = mProduct.getLossProfitScale();
            String totalProfitStr;
            String totalProfitRmbStr;
            if (totalProfit >= 0) {
                mTotalProfit.setTextColor(ContextCompat.getColor(getContext(), R.color.redPrimary));
                mOneKeyClosePositionBtn.setBackgroundResource(R.drawable.btn_red_one_key_close);
                totalProfitStr = "+" + FinanceUtil.formatWithScale(totalProfit, scale);
                totalProfitRmbStr = "+" + FinanceUtil.formatWithScale(totalProfit * ratio);
            } else {
                mTotalProfit.setTextColor(ContextCompat.getColor(getContext(), R.color.greenPrimary));
                mOneKeyClosePositionBtn.setBackgroundResource(R.drawable.btn_green_one_key_close);
                totalProfitStr = FinanceUtil.formatWithScale(totalProfit, scale);
                totalProfitRmbStr = FinanceUtil.formatWithScale(totalProfit * ratio);
            }
            mTotalProfit.setText(totalProfitStr);
            if (mProduct.isForeign()) {
                mTotalProfitRmb.setText("(" + totalProfitRmbStr + FinanceUtil.UNIT_YUAN + ")");
            }
        } else {
            if (mLossProfitArea.getVisibility() == View.VISIBLE) {
                mLossProfitArea.setVisibility(View.GONE);
            }
        }
    }

    static class HoldingOrderAdapter extends BaseAdapter {

        private Context mContext;
        private Product mProduct;
        private List<HoldingOrder> mHoldingOrderList;

        public HoldingOrderAdapter(Context context, Product product, List<HoldingOrder> holdingOrderList) {
            mContext = context;
            mProduct = product;
            mHoldingOrderList = holdingOrderList;
        }

        public void setHoldingOrderList(List<HoldingOrder> holdingOrderList) {
            mHoldingOrderList = holdingOrderList;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return mHoldingOrderList.size();
        }

        @Override
        public Object getItem(int position) {
            return mHoldingOrderList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.row_holding_order, null);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.bindingData((HoldingOrder) getItem(position), mProduct);
            return convertView;
        }

        static class ViewHolder {
            @BindView(R.id.buyOrSell)
            TextView mBuyOrSell;
            @BindView(R.id.hands)
            TextView mHands;
            @BindView(R.id.lossProfit)
            TextView mLossProfit;
            @BindView(R.id.buyPrice)
            TextView mBuyPrice;
            @BindView(R.id.stopProfit)
            TextView mStopProfit;
            @BindView(R.id.lastPrice)
            TextView mLastPrice;
            @BindView(R.id.stopLoss)
            TextView mStopLoss;
            @BindView(R.id.closePositionButton)
            TextView mClosePositionButton;

            ViewHolder(View view) {
                ButterKnife.bind(this, view);
            }

            public void bindingData(HoldingOrder item, Product product) {
                if (item.getDirection() == HoldingOrder.DIRECTION_LONG) {
                    mBuyOrSell.setText(R.string.bullish);
                } else {
                    mBuyOrSell.setText(R.string.bearish);
                }
                mHands.setText(item.getHandsNum() + "手");

                mBuyPrice.setText(FinanceUtil.formatWithScale(item.getRealAvgPrice(), product.getPriceDecimalScale()));
                mStopProfit.setText(FinanceUtil.formatWithScale(item.getStopWin(), product.getLossProfitScale())
                        + product.getCurrencyUnit());
                mStopLoss.setText(FinanceUtil.formatWithScale(item.getStopLoss(), product.getLossProfitScale())
                        + product.getCurrencyUnit());
                mClosePositionButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
            }
        }
    }
}
