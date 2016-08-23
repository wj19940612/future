package com.jnhyxx.html5.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jnhyxx.html5.R;
import com.jnhyxx.html5.view.OrderConfigurationSelector;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


public class PlaceOrderFragment extends BaseFragment {

    @BindView(R.id.tradeQuantitySelector)
    OrderConfigurationSelector mTradeQuantitySelector;

    private OnBuyBtnClickListener mListener;

    private static final String TYPE = "fragmentType";
    public static final int TYPE_BUY_LONG = 0;
    public static final int TYPE_SELL_SHORT = 1;
    public static final String TAG = "PlaceOrder";

    private int mType;

    private Unbinder mBinder;

    public static PlaceOrderFragment newInstance(int type) {
        PlaceOrderFragment fragment = new PlaceOrderFragment();
        Bundle args = new Bundle();
        args.putInt(TYPE, type);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnBuyBtnClickListener) {
            mListener = (OnBuyBtnClickListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnBuyBtnClickListener");
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mType = getArguments().getInt(TYPE, 0);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_place_order, container, false);
        mBinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        final List<Test> testList = new ArrayList<>();
        testList.add(new Test("1手"));
        testList.add(new Test("2手"));
        testList.add(new Test("5手"));
        testList.add(new Test("10手"));
        testList.add(new Test("15手"));
        testList.add(new Test("15手"));
        testList.add(new Test("15手"));
        mTradeQuantitySelector.setOrderConfigurationList(testList);
        mTradeQuantitySelector.setOnItemClickListener(new OrderConfigurationSelector.OnItemClickListener() {
            @Override
            public void onItemClick(OrderConfigurationSelector.OrderConfiguration configuration, int position) {
                Log.d("TEST", "onItemClick: " + position);
            }
        });
    }

    private class Test implements OrderConfigurationSelector.OrderConfiguration {

        private String value;

        public Test(String value) {
            this.value = value;
        }

        @Override
        public String getValue() {
            return value;
        }
    }

    public void onButtonPressed() {
        if (mListener != null) {
            mListener.onBuyBtnClick();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBinder.unbind();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnBuyBtnClickListener {
        void onBuyBtnClick();
    }
}
