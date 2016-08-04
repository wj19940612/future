package com.jnhyxx.html5.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.jnhyxx.html5.R;
import com.jnhyxx.html5.domain.local.ProductPkg;
import com.jnhyxx.html5.domain.local.User;
import com.jnhyxx.html5.domain.market.MarketBrief;
import com.jnhyxx.html5.domain.market.PositionBrief;
import com.jnhyxx.html5.domain.market.Product;
import com.jnhyxx.html5.net.API;
import com.jnhyxx.html5.net.Callback;
import com.jnhyxx.html5.net.Resp;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class HallFragment extends BaseFragment {

    @BindView(android.R.id.list)
    ListView mList;

    private Unbinder mBinder;

    private List<ProductPkg> mProductPkgList;
    private List<PositionBrief> mPositionBriefList;
    private List<MarketBrief> mMarketBriefList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_hall, container, false);
        mBinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mProductPkgList = new ArrayList<>();

        requestProductList();
        API.Market.getProductMarketBreifList()
                .setCallback(new Callback<Resp<List<MarketBrief>>>() {
                    @Override
                    public void onSuccess(Resp<List<MarketBrief>> listResp) {
                        if (listResp.isSuccess()) {
                            ProductPkg.updateProductPkgList()
                        }
                    }
                }).setTag(TAG).post();
    }

    private void requestProductList() {
        API.Market.getProductList().setTag(TAG).setIndeterminate(this)
                .setCallback(new Resp.Callback<Resp<List<Product>>, List<Product>>() {
                    @Override
                    public void onRespSuccess(List<Product> products) {
                        ProductPkg.updateProductPkgList(mProductPkgList, products,
                                mPositionBriefList, mMarketBriefList);
                    }
                }).post();
    }

    @Override
    public void onResume() {
        super.onResume();
        requestPositionBriefList();
    }

    private void requestPositionBriefList() {
        if (User.getUser().isLogin()) {
            API.Order.getOrderPositionList(User.getUser().getToken())
                    .setCallback(new Resp.Callback<Resp<List<PositionBrief>>, List<PositionBrief>>() {
                        @Override
                        public void onRespSuccess(List<PositionBrief> positionBriefs) {
                            mPositionBriefList = positionBriefs;
                            boolean updateProductList =
                                    ProductPkg.updateProductPkgList(mProductPkgList, mPositionBriefList);
                            if (updateProductList) {
                                requestProductList();
                            }
                        }
                    }).setTag(TAG).post();
        } else { // clear all product position
            ProductPkg.clearPositionBriefs(mProductPkgList);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBinder.unbind();
    }
}
