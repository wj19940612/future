package com.jnhyxx.html5.utils;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.TypedValue;
import android.view.View;

/**
 * Created by MR.YANG on 2017/2/18.
 */

public class DividerGridItemDecoration extends RecyclerView.ItemDecoration {
    private static final int[] ATTRS = new int[]{android.R.attr.listDivider};
    private Drawable mDivider;
    private int mHorizontalMargin;
    private int mVerticalMargin;

    public DividerGridItemDecoration(Context context) {
        final TypedArray a = context.obtainStyledAttributes(ATTRS);
        mDivider = a.getDrawable(0);
        mHorizontalMargin = dip2px(context, 16);
        mVerticalMargin = dip2px(context, 10);
        a.recycle();
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        drawHorizontal(c, parent);
        drawVertical(c, parent);
    }

    private int getSpanCount(RecyclerView parent) {
        // 列数
        int spanCount = -1;
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {

            spanCount = ((GridLayoutManager) layoutManager).getSpanCount();
        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            spanCount = ((StaggeredGridLayoutManager) layoutManager)
                    .getSpanCount();
        }
        return spanCount;
    }

    public void drawHorizontal(Canvas c, RecyclerView parent) {
        int childCount = parent.getChildCount();
        int spanCount = getSpanCount(parent);
        int position = childCount - (childCount - 1) % spanCount;
        if ((childCount - 1) % spanCount == 0) {
            position -= spanCount;
        }
        for (int i = 0; i < childCount; i++) {
            if (i == position) {
                break;
            }
            final View child = parent.getChildAt(i);
            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
            final int left = child.getLeft() - params.leftMargin;
            final int right = child.getRight() + params.rightMargin + mDivider.getIntrinsicWidth();

            final int top = child.getBottom() + params.topMargin;
            final int bottom = top + mDivider.getIntrinsicHeight();
            if (i % spanCount == 1) {
                mDivider.setBounds(left + mHorizontalMargin, top, right + mDivider.getIntrinsicHeight(), bottom);
            }
            if (i % spanCount == 2) {
                mDivider.setBounds(left, top, right, bottom);
            }

            if (i % spanCount == 0) {
                mDivider.setBounds(left, top, right - mHorizontalMargin + mDivider.getIntrinsicHeight(), bottom);
            }
            if (i == 0) {
                mDivider.setBounds(left, top, right, bottom);
            }
            mDivider.draw(c);
        }
    }

    public void drawVertical(Canvas c, RecyclerView parent) {
        int childCount = parent.getChildCount();
        int spanCount = getSpanCount(parent);
        for (int i = 0; i < childCount; i++) {
            if (i % spanCount == 0) {
                continue;
            }
            final View child = parent.getChildAt(i);

            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child
                    .getLayoutParams();
            final int top = child.getTop() - params.topMargin + mVerticalMargin;
            final int bottom = child.getBottom() + params.bottomMargin - mVerticalMargin;
            final int left = child.getRight() + params.rightMargin;
            final int right = left + mDivider.getIntrinsicWidth();
            mDivider.setBounds(left, top, right, bottom);
            mDivider.draw(c);
        }
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        outRect.set(mDivider.getIntrinsicHeight(), mDivider.getIntrinsicHeight(), mDivider.getIntrinsicHeight(), mDivider.getIntrinsicHeight());
    }

    /**
     * dp2px
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * px2dp
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 根据设备信息获取当前分辨率下指定单位对应的像素大小；
     * px,dip,sp -> px
     */
    public float getRawSize(Context c, int unit, float size) {
        Resources r;
        if (c == null) {
            r = Resources.getSystem();
        } else {
            r = c.getResources();
        }
        return TypedValue.applyDimension(unit, size, r.getDisplayMetrics());
    }
}
