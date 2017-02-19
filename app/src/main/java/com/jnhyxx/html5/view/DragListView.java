package com.jnhyxx.html5.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.support.annotation.FloatRange;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.jnhyxx.html5.R;

/**
 * Created by MR.YANG on 2017/2/17.
 */

public class DragListView extends ListView {
    private static final String LOG_TAG = "DragListView";

    /**
     * 拖拽快照的透明度(0.0f ~ 1.0f)。
     */
    private static final float DRAG_PHOTO_VIEW_ALPHA = .8f;

    /**
     * 上下滚动时的时间
     */
    private static final int SMOOTH_SCROLL_DURATION = 100;

    /**
     * 上下滚动时的最大距离，可进行设置
     *
     * @see # setMaxDistance(int)
     * @see # getMaxDistance()
     */
    private int mMaxDistance = 30;

    /**
     * 是否处于拖拽中
     */
    private boolean mIsDraging;

    /**
     * 按下时的坐标位置
     */
    private int mDownX;
    private int mDownY;

    /**
     * 移动时的坐标
     */
    private int mMoveX;
    private int mMoveY;

    /**
     * 原生偏移量。也就是ListView的左上角相对于屏幕的位置
     */
    private int mRawOffsetX;
    private int mRawOffsetY;

    /**
     * 在条目中的位置
     */
    private int mItemOffsetX;
    private int mItemOffsetY;

    /**
     * 拖拽快照的垂直位置范围。根据条目数量和ListView的高度来确定
     */
    private int mMinDragY;
    private int mMaxDragY;

    /**
     * 拖拽条目的高度
     */
    private int mDragItemHeight;

    /**
     * 被拖拽的条目位置
     */
    private int mDragPosition;

    /**
     * 移动前的条目位置
     */
    private int mFromPosition;

    /**
     * 移动后的条目位置
     */
    private int mToPosition;

    /**
     * 窗口管理器，用于显示条目的快照
     */
    private WindowManager mWindowManager;

    /**
     * 窗口管理的布局参数
     */
    private WindowManager.LayoutParams mWindowLayoutParams;

    /**
     * 拖拽条目的快照图片
     */
    private Bitmap mDragPhotoBitmap;

    /**
     * 正在拖拽的条目快照view
     */
    private ImageView mDragPhotoView;
    private int[] mTempLoc = new int[2];
    private View mItemView;
    private int mDragHandleId;


    public DragListView(Context context) {
        super(context);
    }

    public DragListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (attrs != null) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.DragListView, 0, 0);

            mDragHandleId = typedArray.getResourceId(R.styleable.DragListView_dragHandle, 0);
        }
    }

    public DragListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        // 获取第一个手指点的Action
        int action = ev.getAction() & MotionEvent.ACTION_MASK;
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mDownX = (int) ev.getX();
                mDownY = (int) ev.getY();
                // 获取当前触摸位置对应的条目索引
                mDragPosition = pointToPosition(mDownX, mDownY);

                int headerViewsCount = getHeaderViewsCount();
                int footerViewsCount = getFooterViewsCount();
                if (mDragPosition != AdapterView.INVALID_POSITION && mDragPosition > headerViewsCount
                        && mDragPosition < (getCount() - footerViewsCount)) {
                    mItemView = getChildAt(mDragPosition - getFirstVisiblePosition());
                    int rawX = (int) ev.getRawX();
                    int rawY = (int) ev.getRawY();
                    View view = mItemView.findViewById(mDragHandleId);
                    if (view != null && view.getVisibility() == VISIBLE) {
                        view.getLocationOnScreen(mTempLoc);

                        if (rawX > mTempLoc[0] && rawY > mTempLoc[1] &&
                                rawX < mTempLoc[0] + view.getWidth() &&
                                rawY < mTempLoc[1] + view.getHeight()) {
                            mIsDraging = true;
                            mToPosition = mFromPosition = mDragPosition;

                            mRawOffsetX = (int) (ev.getRawX() - mDownX);
                            mRawOffsetY = (int) (ev.getRawY() - mDownY);

                            // 开始拖拽的前期工作：展示item快照
                            startDrag();
                        }
                    }
                } else {
                    return super.onTouchEvent(ev);
                }
                break;

            case MotionEvent.ACTION_MOVE:
                mMoveX = (int) ev.getX();
                mMoveY = (int) ev.getY();
                if (mIsDraging) {
                    // 更新快照位置
                    updateDragView();
                    // 更新当前被替换的位置
                    updateItemView();
                } else {
                    return super.onTouchEvent(ev);
                }
                break;

            case MotionEvent.ACTION_UP:
                if (mIsDraging) {
                    // 停止拖拽
                    stopDrag();
                } else {
                    return super.onTouchEvent(ev);
                }
                break;
            default:
                break;
        }
        return true;
    }

    /**
     * 开始拖拽
     */

    private boolean startDrag() {
        // 实际在ListView中的位置，因为涉及到条目的复用
        final View itemView = getItemView(mDragPosition);
        if (itemView == null) {
            return false;
        }
        // 进行绘图缓存
        itemView.setDrawingCacheEnabled(true);
        // 提取缓存中的图片
        mDragPhotoBitmap = Bitmap.createBitmap(itemView.getDrawingCache());
        // 清除绘图缓存，否则复用的时候，会出现前一次的图片。或使用销毁destroyDrawingCache()
        itemView.setDrawingCacheEnabled(false);

        // 隐藏。为了防止隐藏时出现画面闪烁，使用动画去除闪烁效果
        Animation aAnim = new AlphaAnimation(1f, DRAG_PHOTO_VIEW_ALPHA);
        aAnim.setDuration(50);
        aAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                // Move中有隐藏的功能，如果按下后快速移动，会出现该显示的又被隐藏了。所以要作判断
                if (mIsDraging && mToPosition == mDragPosition) {
                    itemView.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        itemView.startAnimation(aAnim);

        mItemOffsetX = mDownX - itemView.getLeft();
        mItemOffsetY = mDownY - itemView.getTop();
        mDragItemHeight = itemView.getHeight();
        mMinDragY = mRawOffsetY + getChildAt(getAdapter().headCount()).getTop();
        // 根据是否显示完全，设定快照在Y轴上可拖到的最大值
        if (isShowAll()) {
            mMaxDragY = mRawOffsetY + getChildAt(getAdapter().getDragCount() + getAdapter().headCount() - 1).getTop();
        } else {
            mMaxDragY = mRawOffsetY + getHeight() - mDragItemHeight;
        }
        createDragPhotoView();
        return true;
    }

    /**
     * 判断ListView是否全部显示，即ListView无法上下滚动了
     */
    private boolean isShowAll() {
        if (getChildCount() == 0) {
            return true;
        }
        View firstChild = getChildAt(0);
        int itemAllHeight = firstChild.getBottom() - firstChild.getTop() + getDividerHeight();
        return itemAllHeight * (getAdapter().getDragCount() + getAdapter().headCount()) < getHeight();
    }

    /**
     * 创建拖拽快照
     */
    private void createDragPhotoView() {
        // 获取当前窗口管理器
        mWindowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        // 创建布局参数
        mWindowLayoutParams = new WindowManager.LayoutParams();
        mWindowLayoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        mWindowLayoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        mWindowLayoutParams.gravity = Gravity.TOP | Gravity.START;
        mWindowLayoutParams.format = PixelFormat.TRANSLUCENT; // 期望的图片为半透明效果，但设置其他值并没有看到不一样的效果
        // 下面这些参数能够帮助准确定位到选中项点击位置
        mWindowLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
        mWindowLayoutParams.windowAnimations = 0; // 无动画
        mWindowLayoutParams.alpha = DRAG_PHOTO_VIEW_ALPHA; // 微透明

        mWindowLayoutParams.x = mDownX + mRawOffsetX - mItemOffsetX;
        mWindowLayoutParams.y = adjustDragY(mDownY + mRawOffsetY - mItemOffsetY);

        mDragPhotoView = new ImageView(getContext());
        mDragPhotoView.setImageBitmap(mDragPhotoBitmap);
        mWindowManager.addView(mDragPhotoView, mWindowLayoutParams);
    }

    /**
     * 校正Drag的值，不让其越界
     */
    private int adjustDragY(int y) {
        if (y < mMinDragY) {
            return mMinDragY;
        } else if (y > mMaxDragY) {
            return mMaxDragY;
        }
        return y;
    }

    /**
     * 根据Adapter中的位置获取对应ListView的条目
     */
    private View getItemView(int position) {
        if (position < getAdapter().headCount() || position > getAdapter().getDragCount() + getAdapter().headCount()) {
            return null;
        }
        int index = position - getFirstVisiblePosition();
        return getChildAt(index);
    }

    /**
     * 更新快照的位置
     */
    private void updateDragView() {
        if (mDragPhotoView != null) {
            mWindowLayoutParams.y = adjustDragY(mMoveY + mRawOffsetY - mItemOffsetY);
            mWindowManager.updateViewLayout(mDragPhotoView, mWindowLayoutParams);
        }
    }

    /**
     * 更新条目位置、显示等
     */
    private void updateItemView() {
        int position = pointToPosition(mMoveX, mMoveY);
        if (position != AdapterView.INVALID_POSITION) {
            if (position < (getAdapter().getDragCount() + getAdapter().headCount()) && position > 0) {
                mToPosition = position;
            }
        }

        // 调换位置，并把显示进行调换
        if (mFromPosition != mToPosition) {
            if (exchangePosition()) {
                View view = getItemView(mFromPosition);
                if (view != null) {
                    view.setVisibility(View.VISIBLE);
                }
                view = getItemView(mToPosition);
                if (view != null) {
                    view.setVisibility(View.INVISIBLE);
                }
                mFromPosition = mToPosition;
            }
        }

        // 如果当前位置已经不到一个条目，则进行上或下的滚动。并根据距离边界的距离，设定滚动速度
        int dragY = mMoveY - mItemOffsetY;
        if (dragY < mDragItemHeight) {
            int value = Math.max(0, dragY); // 防越界
            float percent = estimatePercent(mDragItemHeight, 0, value);
            int distance = estimateInt(0, -mMaxDistance, percent);
            smoothScrollBy(distance, SMOOTH_SCROLL_DURATION);
        } else if (dragY > getHeight() - 2 * mDragItemHeight) {
            int value = Math.max(0, getHeight() - dragY - mDragItemHeight); // 防越界
            float percent = estimatePercent(mDragItemHeight, 0, value);
            int distance = estimateInt(0, mMaxDistance, percent);
            smoothScrollBy(distance, SMOOTH_SCROLL_DURATION);
        }
    }

    /**
     * 停止拖拽
     */
    private void stopDrag() {
        // 显示坐标上的条目
        View view = getItemView(mToPosition);
        if (view != null) {
            view.setVisibility(View.VISIBLE);
        }
        // 移除快照
        if (mDragPhotoView != null) {
            mWindowManager.removeView(mDragPhotoView);
            mDragPhotoView.setImageDrawable(null);
            mDragPhotoBitmap.recycle();
            mDragPhotoBitmap = null;
            mDragPhotoView = null;
        }
        mIsDraging = false;
    }

    /**
     * 调换位置
     */
    private boolean exchangePosition() {
        int itemCount = getAdapter().getDragCount() + getAdapter().headCount();
        if (mFromPosition >= getAdapter().headCount() && mFromPosition < itemCount
                && mToPosition >= getAdapter().headCount() && mToPosition < itemCount) {
            getAdapter().swapData(mFromPosition - getAdapter().headCount(), mToPosition - getAdapter().headCount());
            return true;
        }
        return false;
    }


    /**
     * 根据百分比，估算在指定范围内的值
     */
    public static int estimateInt(int start, int end, @FloatRange(from = 0.0f, to = 1.0f) float percent) {
        return (int) (start + percent * (end - start));
    }

    /**
     * 估算给定值在指定范围内的百分比
     *
     * @param start 始值
     * @param end   末值
     * @param value 要估算的值
     * @return 0.0f ~ 1.0f。如果没有指定范围，或给定值不在范围内则返回-1
     */
    public static float estimatePercent(float start, float end, float value) {
        if (start == end
                || (value < start && value < end)
                || (value > start && value > end)) {
            return -1;
        }
        return (value - start) / (end - start);
    }

    @Override
    public void setAdapter(ListAdapter adapter) {
        if (!(adapter instanceof DragListViewAdapter)) {
            throw new RuntimeException("请使用自带的Adapter");
        }
        super.setAdapter(adapter);
    }

    @Override
    public DragListViewAdapter getAdapter() {
        return (DragListViewAdapter) super.getAdapter();
    }
}
