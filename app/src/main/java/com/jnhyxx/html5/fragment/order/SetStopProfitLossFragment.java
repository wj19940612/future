package com.jnhyxx.html5.fragment.order;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jnhyxx.html5.domain.market.Product;
import com.jnhyxx.html5.domain.order.HoldingOrder;
import com.jnhyxx.html5.fragment.BaseFragment;

public class SetStopProfitLossFragment extends BaseFragment {

    private static final String ORDER = "order";

    private HoldingOrder mHoldingOrder;
    private Product mProduct;
    private int mFundType;

    public static SetStopProfitLossFragment newInstance(Product product, int fundType, HoldingOrder order) {
        SetStopProfitLossFragment fragment = new SetStopProfitLossFragment();
        Bundle args = new Bundle();
        args.putSerializable(Product.EX_PRODUCT, product);
        args.putInt(Product.EX_FUND_TYPE, fundType);
        args.putParcelable(ORDER, order);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        if (getArguments() != null) {
            mProduct = (Product) getArguments().getSerializable(Product.EX_PRODUCT);
            mFundType = getArguments().getInt(Product.EX_FUND_TYPE);
            mHoldingOrder = getArguments().getParcelable(ORDER);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }
}
