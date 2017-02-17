package com.jnhyxx.html5.activity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jnhyxx.html5.R;
import com.jnhyxx.html5.domain.market.Product;
import com.jnhyxx.html5.net.API;
import com.jnhyxx.html5.net.Callback2;
import com.jnhyxx.html5.net.Resp;
import com.jnhyxx.html5.view.DragAdapter;
import com.jnhyxx.html5.view.DragSortListView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by MR.YANG on 2017/2/16.
 */

public class ProductOptionalActivity extends BaseActivity {
    @BindView(android.R.id.list)
    DragSortListView list;
    private List<Product> mProductList = new ArrayList<Product>();
    private MyAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_optional);
        ButterKnife.bind(this);
        requestProductList();
        adapter = new MyAdapter(this, mProductList);
        list.setAdapter(adapter);
    }

    private void requestProductList() {
        API.Market.getProductList().setTag(TAG).setIndeterminate(this)
                .setCallback(new Callback2<Resp<List<Product>>, List<Product>>() {
                    @Override
                    public void onRespSuccess(List<Product> products) {
                        mProductList.addAll(products);
                        adapter.notifyDataSetChanged();
                    }
                }).fire();
    }

    class MyAdapter extends DragAdapter<Product> {
        ViewHolder holder;

        public MyAdapter(Context context, List<Product> list) {
            super(context, list);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                holder = new ViewHolder();
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.bindData(mProductList.get(position).getVarietyName());
            return holder.rootView;
        }

        class ViewHolder {

            public View rootView;
            @BindView(R.id.check)
            ImageView check;
            @BindView(R.id.name)
            TextView name;
            @BindView(R.id.touch)
            ImageView touch;

            ViewHolder() {
                rootView = View.inflate(ProductOptionalActivity.this, R.layout.row_product_optional, null);
                ButterKnife.bind(this, rootView);
                rootView.setTag(this);
            }

            public void bindData(String varietyName) {
                name.setText(varietyName);
            }
        }
    }
}
