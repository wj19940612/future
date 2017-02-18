package com.jnhyxx.html5.activity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jnhyxx.html5.App;
import com.jnhyxx.html5.Preference;
import com.jnhyxx.html5.R;
import com.jnhyxx.html5.domain.market.Product;
import com.jnhyxx.html5.net.API;
import com.jnhyxx.html5.net.Callback2;
import com.jnhyxx.html5.net.Resp;
import com.jnhyxx.html5.utils.ToastUtil;
import com.jnhyxx.html5.view.DragListView;
import com.jnhyxx.html5.view.DragListViewAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by MR.YANG on 2017/2/16.
 */

public class ProductOptionalActivity extends BaseActivity {
    @BindView(android.R.id.list)
    DragListView list;
    private List<String> mProductOptionals = Arrays.asList(new String[]{"10", "11", "8"});
    private List<Product> mProductList1 = new ArrayList<>();
    private List<Product> mProductList2 = new ArrayList<>();
    private MyAdapter mAdapter;
    private boolean mIsDomestic;
    private String mProductOptional;

    public static final int REQ_CODE_RESULT = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_optional);
        ButterKnife.bind(this);
        mIsDomestic = getIntent().getBooleanExtra("isDomestic", false);
        if (mIsDomestic) {
            mProductOptional = Preference.get().getProductOptionalDomestic();
        } else {
            mProductOptional = Preference.get().getProductOptionalForeign();
        }
        List<String> strings = Arrays.asList(mProductOptional.split(","));
        if (strings.size() > 0) {
            mProductOptionals = strings;
        }
        requestProductList();
        mAdapter = new MyAdapter(this, mProductList1, mProductList2);
        list.setAdapter(mAdapter);
    }

    @Override
    public void onBackPressed() {
        setResult(REQ_CODE_RESULT);
        StringBuilder sb = new StringBuilder();
        for (Product product : mProductList1) {
            sb.append(product.getVarietyId()).append(",");
        }
        if (mIsDomestic) {
            Preference.get().setProductOptionalDomestic(sb.deleteCharAt(sb.length() - 1).toString());
        } else {
            Preference.get().setProductOptionalForeign(sb.deleteCharAt(sb.length() - 1).toString());
        }
        super.onBackPressed();
    }

    private void requestProductList() {
        API.Market.getProductList().setTag(TAG).setIndeterminate(this)
                .setCallback(new Callback2<Resp<List<Product>>, List<Product>>() {
                    @Override
                    public void onRespSuccess(List<Product> products) {
                        for (int i = 0; i < mProductOptionals.size(); i++) {
                            for (int j = 0; j < products.size(); j++) {
                                Product product = products.get(j);
                                if ((String.valueOf(product.getVarietyId())).equals(mProductOptionals.get(i))) {
                                    product.setIsOptional(true);
                                    mProductList1.add(product);
                                    products.remove(j);
                                    break;
                                }
                            }
                        }
                        ListIterator<Product> productListIterator = products.listIterator();
                        while (productListIterator.hasNext()) {
                            Product product = productListIterator.next();
                            product.setIsOptional(false);
                            if (product.isDomestic() == mIsDomestic) {
                                mProductList2.add(product);
                            }
                        }
                        mAdapter.notifyDataSetChanged();
                    }
                }).fire();
    }

    class MyAdapter extends DragListViewAdapter<Product> {
        ViewHolder mViewHolder;

        public MyAdapter(Context context, List<Product> list1, List<Product> list2) {
            super(context, list1, list2);
        }

        @Override
        public int getCount() {
            return mDragData1.size() + mDragData2.size() + 2;
        }

        @Override
        public View getItemView(int position, View convertView, ViewGroup parent) {
            if (position == 0 || position == mDragData1.size() + 1) {
                convertView = View.inflate(App.getAppContext(), R.layout.row_optional_title, null);
                TextView optionalTitle = (TextView) convertView.findViewById(R.id.optionalTitle);
                TextView dragInfo = (TextView) convertView.findViewById(R.id.dragInfo);
                if (position == 0) {
                    optionalTitle.setText("品种自选");
                    dragInfo.setVisibility(View.VISIBLE);
                } else {
                    if (mIsDomestic) {
                        optionalTitle.setText("国内期货");
                    } else {
                        optionalTitle.setText("国际期货");
                    }
                    dragInfo.setVisibility(View.INVISIBLE);
                }
                return convertView;
            }

            if (convertView != null && convertView instanceof LinearLayout) {
                mViewHolder = (ViewHolder) convertView.getTag();
            } else {
                mViewHolder = new ViewHolder();
            }

            int tempPosition;
            Product product = new Product();
            if (position >= mDragData1.size() + 2) {
                tempPosition = position - mDragData1.size() - 2;
                product = mDragData2.get(tempPosition);
            } else if (position >= 1) {
                tempPosition = position - 1;
                product = mDragData1.get(tempPosition);
            }
            mViewHolder.bindData(product, position);
            return mViewHolder.holderView;
        }

        @Override
        public boolean isEnabled(int position) {
            if (position == 0 || position == mDragData1.size() + 1) {
                return false;
            }
            return super.isEnabled(position);
        }

        @Override
        public int headCount() {
            return 1;
        }

        class ViewHolder {
            @BindView(R.id.check)
            ImageView mCheck;
            @BindView(R.id.name)
            TextView mName;
            @BindView(R.id.touch)
            ImageView mTouch;

            public View holderView;

            ViewHolder() {
                holderView = initHolderView();
                ButterKnife.bind(this, holderView);
                holderView.setTag(this);
            }

            private View initHolderView() {
                holderView = View.inflate(App.getAppContext(), R.layout.row_product_optional, null);
                return holderView;
            }

            public void bindData(final Product product, final int position) {
                if (product.getIsOptional()) {
                    mCheck.setSelected(true);
                } else {
                    mCheck.setSelected(false);
                }
                mCheck.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (position > mDragData1.size() + 1) {
                            product.setIsOptional(!product.getIsOptional());
                            mDragData2.remove(product);
                            mDragData1.add(product);
                        } else {
                            if (mDragData1.size() < 2) {
                                ToastUtil.curt("至少得有一个自选品种");
                                return;
                            }
                            product.setIsOptional(!product.getIsOptional());
                            mDragData1.remove(product);
                            mDragData2.add(product);
                        }
                        notifyDataSetChanged();
                    }
                });
                mName.setText(product.getVarietyName());
                if (position > mDragData1.size() + 1) {
                    mTouch.setVisibility(View.INVISIBLE);
                } else {
                    mTouch.setVisibility(View.VISIBLE);
                }
            }
        }
    }
}
