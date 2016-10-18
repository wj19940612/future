package com.jnhyxx.html5.activity;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jnhyxx.html5.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ScoreActivity extends BaseActivity {

    @BindView(R.id.availableGold)
    TextView mAvailableGold;
    @BindView(R.id.goldStoreButton)
    TextView mGoldStoreButton;
    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;
    @BindView(R.id.activity_score)
    LinearLayout mActivityScore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score);
        ButterKnife.bind(this);

    }

    static class GoldProductAdapter extends RecyclerView.Adapter<ProductViewHolder> {

        @Override
        public ProductViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return null;
        }

        @Override
        public void onBindViewHolder(ProductViewHolder holder, int position) {

        }

        @Override
        public int getItemCount() {
            return 0;
        }
    }

    static class ProductViewHolder extends RecyclerView.ViewHolder {

        public ProductViewHolder(View itemView) {
            super(itemView);
        }
    }

    static class GridDividerDecoration extends RecyclerView.ItemDecoration {

        private static final int[] ATTRS = new int[] { android.R.attr.listDivider };
        private Drawable mDivider;

        public GridDividerDecoration(Context context) {
            TypedArray a = context.obtainStyledAttributes(ATTRS);
            mDivider = a.getDrawable(0);
            a.recycle();
        }

        @Override
        public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
            drawVertical(c, parent);
            drawHorizontal(c, parent);
        }

        /** Draw dividers to the right of each child view */
        private void drawHorizontal(Canvas c, RecyclerView parent) {
            final int childCount = parent.getChildCount();

            for (int i = 0; i < childCount; i++) {
                final View child = parent.getChildAt(i);
                final RecyclerView.LayoutParams params =
                        (RecyclerView.LayoutParams) child.getLayoutParams();

                final int left = child.getRight() + params.rightMargin;
                final int right = left + mDivider.getIntrinsicWidth();
                final int top = child.getTop() - params.topMargin;
                final int bottom = child.getBottom() + params.bottomMargin;
                mDivider.setBounds(left, top, right, bottom);
                mDivider.draw(c);
            }
        }

        /** Draw dividers at each expected grid interval */
        private void drawVertical(Canvas c, RecyclerView parent) {
            if (parent.getChildCount() == 0) return;

            final int childCount = parent.getChildCount();

            for (int i = 0; i < childCount; i++) {
                final View child = parent.getChildAt(i);
                final RecyclerView.LayoutParams params =
                        (RecyclerView.LayoutParams) child.getLayoutParams();

                final int left = child.getLeft() - params.leftMargin;
                final int right = child.getRight() + params.rightMargin;
                final int top = child.getBottom() + params.bottomMargin;
                final int bottom = top + mDivider.getIntrinsicHeight();
                mDivider.setBounds(left, top, right, bottom);
                mDivider.draw(c);
            }
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            outRect.set(0, 0, mDivider.getIntrinsicWidth(), mDivider.getIntrinsicHeight());
        }
    }
}
