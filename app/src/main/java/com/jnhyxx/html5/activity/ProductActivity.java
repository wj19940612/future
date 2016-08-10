package com.jnhyxx.html5.activity;

import android.content.Intent;
import android.os.Bundle;

import com.jnhyxx.html5.R;
import com.jnhyxx.html5.activity.order.OrderActivity;
import com.jnhyxx.html5.domain.market.Product;
import com.johnz.kutils.Launcher;

public class ProductActivity extends BaseActivity {

    private Product mProduct;
    private int mFundType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);

        initData(getIntent());

        Launcher.with(this, OrderActivity.class)
                .putExtra(Product.EX_PRODUCT, mProduct)
                .putExtra(Product.EX_FUND_TYPE, mFundType)
                .execute();
    }

    private void initData(Intent intent) {
        mProduct = (Product) intent.getSerializableExtra(Product.EX_PRODUCT);
        mFundType = intent.getIntExtra(Product.EX_FUND_TYPE, 0);
    }
}
