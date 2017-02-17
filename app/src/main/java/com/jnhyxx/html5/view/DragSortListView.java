package com.jnhyxx.html5.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.HeaderViewListAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;

/**
 * Created by tangtang on 15/5/12.
 */
public class DragSortListView extends ListView {
    public DragSortListView(Context context) {
        super(context);
    }

    public DragSortListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DragSortListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * 拖拽的条目position
     */
    int srcposition;
    /**
     * 目标位置
     */
    int desposition;
    private int upBounds;
    private int downBouds;
    private ImageView dragImageView;
    private WindowManager.LayoutParams params;
    //触点相对条目的距离
    private int offsetY;
    private WindowManager windowManager;
    private DragAdapter adapter;
    int headerCount = 0;
    int footCount = 0;

    @Override
    public void setAdapter(ListAdapter adapter) {
        super.setAdapter(adapter);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (getAdapter() instanceof HeaderViewListAdapter) {
                    HeaderViewListAdapter headerViewListAdapter = ((HeaderViewListAdapter) getAdapter());
                    adapter = (DragAdapter) headerViewListAdapter.getWrappedAdapter();

                    footCount = headerViewListAdapter.getFootersCount();
                    headerCount = headerViewListAdapter.getHeadersCount();

                } else {
                    adapter = (DragAdapter) getAdapter();
                }

                //获取相对y坐标
                int y = (int) ev.getY();
                int x = (int) ev.getX();
                int rawY = (int) ev.getRawY();
                srcposition = pointToPosition(0, y);
                desposition = srcposition;
                Log.i("CCCC", srcposition + "");
                if (srcposition < headerCount || srcposition >= headerCount + adapter.getCount()) {
                    return super.dispatchTouchEvent(ev);
                }
                //获取 选中条目的view //这里应该考虑到headerview的情况
                ViewGroup viewItem = (ViewGroup) getChildAt(srcposition - getFirstVisiblePosition());

                if (viewItem == null)
                    return super.dispatchTouchEvent(ev);

                //获取头像 这里 使用的时候 把点击可拖拽的位置一定要放在定义个child位置
                View logo = viewItem.getChildAt(viewItem.getChildCount() - 1);
                if (logo == null)
                    return super.dispatchTouchEvent(ev);

                if (logo.getVisibility() == VISIBLE && x > logo.getLeft() - 10) {
                    viewItem.setDrawingCacheEnabled(true);
                    Bitmap bt = viewItem.getDrawingCache();
                    //设置向上或者向下滚动

                    //超过2/3 则向下滚动
                    downBouds = getHeight() / 3 * 2;
                    //超过1/3 则向上滚动
                    upBounds = getHeight() / 3;

                    offsetY = y - viewItem.getTop();

                    onDrawDown(rawY - offsetY, bt);
                    return true;
                }
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {

        //正在拖动
        if (dragImageView != null) {
            int y;
            int rawY;
            switch (ev.getAction()) {
                //华东
                case MotionEvent.ACTION_MOVE:
                    y = (int) ev.getY();
                    rawY = (int) ev.getRawY();
                    onDrag(rawY - offsetY, y);
                    break;
                case MotionEvent.ACTION_UP:
                    y = (int) ev.getY();
                    onDragUp(y);
                    break;
            }
            return true;
        }
        return super.onTouchEvent(ev);
    }

    //抬起
    private void onDragUp(int y) {
        windowManager.removeView(dragImageView);
        dragImageView = null;
        int tempPosition = pointToPosition(0, y) - headerCount;
        if (tempPosition != INVALID_POSITION) {
            desposition = tempPosition;
        }
        if (y >= getChildAt(getChildCount() - 1 - footCount).getBottom()) {
            desposition = getChildCount() - 1 - footCount - headerCount;
        } else if (y < getChildAt(0 + headerCount).getTop()) {
            desposition = 0;
        }
        if (desposition <= adapter.getCount() - 1 || desposition >= 0) {
            Object obj = adapter.getItem(srcposition - headerCount);
            adapter.remove(srcposition - headerCount);
            adapter.insert(desposition, obj);
        }
    }

    private void onDrag(int py, int y) {
        params.y = py;
        if (dragImageView != null)
            windowManager.updateViewLayout(dragImageView, params);
        int tempPosition = pointToPosition(0, y);
        if (tempPosition != INVALID_POSITION) {
            desposition = tempPosition;
        }
        //需要滑动距离
        int scrollHeight = 0;
        if (y < upBounds) {
            scrollHeight = 10;
        } else if (y > downBouds) {
            scrollHeight = -10;
        }
        if (desposition <= 0) {
            desposition = 0;
        } else if (desposition >= getChildCount() - 1) {
            desposition = getChildCount() - 1;
        }
        if (scrollHeight != 0) {
            int dragDestItemY = getChildAt(desposition - getFirstVisiblePosition()).getTop();
            int dy = dragDestItemY + scrollHeight;
            setSelectionFromTop(desposition, dy);
        }
    }

    private void onDrawDown(int pYsitionT, Bitmap bitmap) {
        windowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        params = new WindowManager.LayoutParams();
        params.y = pYsitionT;
        params.x = 0;
        params.gravity = Gravity.TOP;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.format = PixelFormat.TRANSLUCENT;
        params.windowAnimations = 0;
        params.type = WindowManager.LayoutParams.TYPE_TOAST;
        params.flags = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;
        params.alpha = 0.8f;
        dragImageView = new ImageView(getContext());
        dragImageView.setImageBitmap(bitmap);
        windowManager.addView(dragImageView, params);
    }
}
