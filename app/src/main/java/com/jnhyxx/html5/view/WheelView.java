package com.jnhyxx.html5.view;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.annotation.FloatRange;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.Size;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;


public class WheelView extends ScrollView {
    public static final String TAG = WheelView.class.getSimpleName();
//
//    public interface OnWheelViewListener {
//        void onSelected(int selectedIndex, Object item);
//    }
//
//
//    private Context context;
////    private ScrollView scrollView;
//
//    private LinearLayout views;
//
//    public WheelView(Context context) {
//        super(context);
//        init(context);
//    }
//
//    public WheelView(Context context, AttributeSet attrs) {
//        super(context, attrs);
//        init(context);
//    }
//
//    public WheelView(Context context, AttributeSet attrs, int defStyle) {
//        super(context, attrs, defStyle);
//        init(context);
//    }
//
//    //    String[] items;
//    List<String> items;
//
//    private List<String> getItems() {
//        return items;
//    }
//
//    public void setItems(List<String> list) {
//        if (null == items) {
//            items = new ArrayList<String>();
//        }
//        items.clear();
//        items.addAll(list);
//
//        // 前面和后面补全
//        for (int i = 0; i < offset; i++) {
//            items.add(0, "");
//            items.add("");
//        }
//
//        initData();
//
//    }
//
//
//    List<ChannelBank> mChannelBanks;
//
//    public void setItemObjects(List<ChannelBank> channelBankList) {
//        if (mChannelBanks == null) {
//            mChannelBanks = new ArrayList<>();
//        }
//        mChannelBanks.clear();
//        mChannelBanks.addAll(channelBankList);
//
//        for (int i = 0; i < offset; i++) {
//            mChannelBanks.add(0, new ChannelBank());
//            mChannelBanks.add(new ChannelBank());
//
//        }
//
//        initData();
//    }
//
//    private void initData() {
//        displayItemCount = offset * 2 + 1;
//        // TODO: 2016/9/22 原来的
////        for (String item : items) {
////            views.addView(createView(item));
////        }
//        if (mChannelBanks != null && !mChannelBanks.isEmpty()) {
//            for (int i = 0; i < mChannelBanks.size(); i++) {
//                views.addView(createView(mChannelBanks.get(i).getName()));
//            }
//            refreshItemView(0);
//        }
//
//    }
//
//    public static final int OFF_SET_DEFAULT = 1;
//    int offset = OFF_SET_DEFAULT; // 偏移量（需要在最前面和最后面补全）
//
//    public int getOffset() {
//        return offset;
//    }
//
//    public void setOffset(int offset) {
//        this.offset = offset;
//    }
//
//    int displayItemCount; // 每页显示的数量
//
//    int selectedIndex = 1;
//
//    private void init(Context context) {
//        this.context = context;
//
////        scrollView = ((ScrollView)this.getParent());
////        Log.d(TAG, "scrollview: " + scrollView);
//        Log.d(TAG, "parent: " + this.getParent());
////        this.setOrientation(VERTICAL);
//        this.setVerticalScrollBarEnabled(false);
//
//        views = new LinearLayout(context);
//        views.setOrientation(LinearLayout.VERTICAL);
//        this.addView(views);
//
//        scrollerTask = new Runnable() {
//
//            public void run() {
//
//                int newY = getScrollY();
//                if (initialY - newY == 0) { // stopped
//                    final int remainder = initialY % itemHeight;
//                    final int divided = initialY / itemHeight;
////                    Log.d(TAG, "initialY: " + initialY);
////                    Log.d(TAG, "remainder: " + remainder + ", divided: " + divided);
//                    if (remainder == 0) {
//                        selectedIndex = divided + offset;
//
//                        onSeletedCallBack();
//                    } else {
//                        if (remainder > itemHeight / 2) {
//                            WheelView.this.post(new Runnable() {
//                                @Override
//                                public void run() {
//                                    WheelView.this.smoothScrollTo(0, initialY - remainder + itemHeight);
//                                    selectedIndex = divided + offset + 1;
//                                    onSeletedCallBack();
//                                }
//                            });
//                        } else {
//                            WheelView.this.post(new Runnable() {
//                                @Override
//                                public void run() {
//                                    WheelView.this.smoothScrollTo(0, initialY - remainder);
//                                    selectedIndex = divided + offset;
//                                    onSeletedCallBack();
//                                }
//                            });
//                        }
//
//
//                    }
//
//
//                } else {
//                    initialY = getScrollY();
//                    WheelView.this.postDelayed(scrollerTask, newCheck);
//                }
//            }
//        };
//
//
//    }
//
//    int initialY;
//
//    Runnable scrollerTask;
//    int newCheck = 50;
//
//    public void startScrollerTask() {
//
//        initialY = getScrollY();
//        this.postDelayed(scrollerTask, newCheck);
//    }
//
//
//    int itemHeight = 0;
//
//    private TextView createView(String item) {
//        TextView tv = new TextView(context);
//        tv.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
//        tv.setSingleLine(true);
//        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
//        tv.setText(item);
//        tv.setGravity(Gravity.CENTER);
//        int padding = dip2px(15);
//        tv.setPadding(padding, padding, padding, padding);
//        if (0 == itemHeight) {
//            itemHeight = getViewMeasuredHeight(tv);
//            Log.d(TAG, "itemHeight: " + itemHeight);
//            views.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, itemHeight * displayItemCount));
//            LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) this.getLayoutParams();
//            LinearLayout.LayoutParams layoutParams;
//            if (lp != null) {
//                layoutParams = new LinearLayout.LayoutParams(lp.width, itemHeight * displayItemCount);
//            } else {
//                layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, itemHeight * displayItemCount);
//            }
//            this.setLayoutParams(layoutParams);
//        }
//        return tv;
//    }
//
//
//    @Override
//    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
//        super.onScrollChanged(l, t, oldl, oldt);
//
////        Log.d(TAG, "l: " + l + ", t: " + t + ", oldl: " + oldl + ", oldt: " + oldt);
//
////        try {
////            Field field = ScrollView.class.getDeclaredField("mScroller");
////            field.setAccessible(true);
////            OverScroller mScroller = (OverScroller) field.get(this);
////
////
////            if(mScroller.isFinished()){
////                Log.d(TAG, "isFinished...");
////            }
////
////        } catch (Exception e) {
////            e.printStackTrace();
////        }
//
//
//        refreshItemView(t);
//
//        if (t > oldt) {
////            Log.d(TAG, "向下滚动");
//            scrollDirection = SCROLL_DIRECTION_DOWN;
//        } else {
////            Log.d(TAG, "向上滚动");
//            scrollDirection = SCROLL_DIRECTION_UP;
//
//        }
//
//
//    }
//
//    private void refreshItemView(int y) {
//        int position = y / itemHeight + offset;
//        int remainder = y % itemHeight;
//        int divided = y / itemHeight;
//
//        if (remainder == 0) {
//            position = divided + offset;
//        } else {
//            if (remainder > itemHeight / 2) {
//                position = divided + offset + 1;
//            }
//
////            if(remainder > itemHeight / 2){
////                if(scrollDirection == SCROLL_DIRECTION_DOWN){
////                    position = divided + offset;
////                    Log.d(TAG, ">down...position: " + position);
////                }else if(scrollDirection == SCROLL_DIRECTION_UP){
////                    position = divided + offset + 1;
////                    Log.d(TAG, ">up...position: " + position);
////                }
////            }else{
//////                position = y / itemHeight + offset;
////                if(scrollDirection == SCROLL_DIRECTION_DOWN){
////                    position = divided + offset;
////                    Log.d(TAG, "<down...position: " + position);
////                }else if(scrollDirection == SCROLL_DIRECTION_UP){
////                    position = divided + offset + 1;
////                    Log.d(TAG, "<up...position: " + position);
////                }
////            }
////        }
//
////        if(scrollDirection == SCROLL_DIRECTION_DOWN){
////            position = divided + offset;
////        }else if(scrollDirection == SCROLL_DIRECTION_UP){
////            position = divided + offset + 1;
//        }
//
//        int childSize = views.getChildCount();
//        for (int i = 0; i < childSize; i++) {
//            TextView itemView = (TextView) views.getChildAt(i);
//            if (null == itemView) {
//                return;
//            }
//            if (position == i) {
//                itemView.setTextColor(Color.parseColor("#0288ce"));
//            } else {
//                itemView.setTextColor(Color.parseColor("#bbbbbb"));
//            }
//        }
//    }
//
//    /**
//     * 获取选中区域的边界
//     */
//    int[] selectedAreaBorder;
//
//    private int[] obtainSelectedAreaBorder() {
//        if (null == selectedAreaBorder) {
//            selectedAreaBorder = new int[2];
//            selectedAreaBorder[0] = itemHeight * offset;
//            selectedAreaBorder[1] = itemHeight * (offset + 1);
//        }
//        return selectedAreaBorder;
//    }
//
//
//    private int scrollDirection = -1;
//    private static final int SCROLL_DIRECTION_UP = 0;
//    private static final int SCROLL_DIRECTION_DOWN = 1;
//
//    Paint paint;
//    int viewWidth;
//
//    @Override
//    public void setBackgroundDrawable(Drawable background) {
//
//        if (viewWidth == 0) {
//            viewWidth = ((Activity) context).getWindowManager().getDefaultDisplay().getWidth();
//            Log.d(TAG, "viewWidth: " + viewWidth);
//        }
//
//        if (null == paint) {
//            paint = new Paint();
//            paint.setColor(Color.parseColor("#83cde6"));
//            paint.setStrokeWidth(dip2px(1f));
//        }
//
//        background = new Drawable() {
//            @Override
//            public void draw(Canvas canvas) {
//                canvas.drawLine(viewWidth * 1 / 6, obtainSelectedAreaBorder()[0], viewWidth * 5 / 6, obtainSelectedAreaBorder()[0], paint);
//                canvas.drawLine(viewWidth * 1 / 6, obtainSelectedAreaBorder()[1], viewWidth * 5 / 6, obtainSelectedAreaBorder()[1], paint);
//            }
//
//            @Override
//            public void setAlpha(int alpha) {
//
//            }
//
//            @Override
//            public void setColorFilter(ColorFilter cf) {
//
//            }
//
//            @Override
//            public int getOpacity() {
//                return 0;
//            }
//        };
//
//
//        super.setBackgroundDrawable(background);
//
//    }
//
//    @Override
//    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
//        super.onSizeChanged(w, h, oldw, oldh);
//        Log.d(TAG, "w: " + w + ", h: " + h + ", oldw: " + oldw + ", oldh: " + oldh);
//        viewWidth = w;
//        setBackgroundDrawable(null);
//    }
//
//    /**
//     * 选中回调
//     */
//    private void onSeletedCallBack() {
//        if (null != onWheelViewListener) {
//            onWheelViewListener.onSelected(selectedIndex, mChannelBanks.get(selectedIndex));
//        }
//
//    }
//
//    public void setSeletion(int position) {
//        final int p = position;
//        selectedIndex = p + offset;
//        this.post(new Runnable() {
//            @Override
//            public void run() {
//                WheelView.this.smoothScrollTo(0, p * itemHeight);
//                onSeletedCallBack();
//            }
//        });
//
//    }
//
//    public String getSeletedItem() {
//        return items.get(selectedIndex);
//    }
//
//    public int getSeletedIndex() {
//        return selectedIndex - offset;
//    }
//
//
//    @Override
//    public void fling(int velocityY) {
//        super.fling(velocityY / 3);
//    }
//
//    @Override
//    public boolean onTouchEvent(MotionEvent ev) {
//        if (ev.getAction() == MotionEvent.ACTION_UP) {
//
//            startScrollerTask();
//        }
//        return super.onTouchEvent(ev);
//    }
//
//    private OnWheelViewListener onWheelViewListener;
//
//    public OnWheelViewListener getOnWheelViewListener() {
//        return onWheelViewListener;
//    }
//
//    public void setOnWheelViewListener(OnWheelViewListener onWheelViewListener) {
//        this.onWheelViewListener = onWheelViewListener;
//    }
//
//    private int dip2px(float dpValue) {
//        final float scale = context.getResources().getDisplayMetrics().density;
//        return (int) (dpValue * scale + 0.5f);
//    }
//
//    private int getViewMeasuredHeight(View view) {
//        int width = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
//        int expandSpec = View.MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, View.MeasureSpec.AT_MOST);
//        view.measure(width, expandSpec);
//        return view.getMeasuredHeight();
//    }

    public static final int TEXT_SIZE = 20;
    public static final int TEXT_PADDING_TOP_BOTTOM = 8;
    public static final int TEXT_PADDING_LEFT_RIGHT = 15;
    public static final int TEXT_COLOR_FOCUS = 0XFF0288CE;
    public static final int TEXT_COLOR_NORMAL = 0XFFBBBBBB;
    public static final int LINE_ALPHA = 150;
    public static final int LINE_COLOR = 0XFF83CDE6;
    public static final float LINE_THICK = 1f;
    public static final int OFF_SET = 2;
    private static final int DELAY = 50;
    private static final int MATCH_PARENT = ViewGroup.LayoutParams.MATCH_PARENT;
    private static final int WRAP_CONTENT = ViewGroup.LayoutParams.WRAP_CONTENT;

    private Context context;
    private LinearLayout views;//容器
    private LinkedList<String> items = new LinkedList<String>();
    private int offset = OFF_SET; // 偏移量（需要在最前面和最后面补全）

    private int displayItemCount; // 每页显示的数量

    private int selectedIndex = OFF_SET;//索引值含补全的占位符的索引
    private int initialY;

    private Runnable scrollerTask = new ScrollerTask();
    private int itemHeight = 0;
    private OnWheelListener onWheelListener;

    private int textSize = TEXT_SIZE;
    private int textColorNormal = TEXT_COLOR_NORMAL;
    private int textColorFocus = TEXT_COLOR_FOCUS;
    private boolean isUserScroll = false;//是否用户手动滚动
    private boolean cycleDisable = false;//是否禁用伪循环
    private float previousY = 0;//记录按下时的Y坐标

    private LineConfig lineConfig;

    public WheelView(Context context) {
        super(context);
        init(context);
    }

    public WheelView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public WheelView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        this.context = context;

        // 2015/12/15 去掉ScrollView的阴影
        setFadingEdgeLength(0);
        setOverScrollMode(OVER_SCROLL_NEVER);
        setVerticalScrollBarEnabled(false);

        views = new LinearLayout(context);
        views.setOrientation(LinearLayout.VERTICAL);
        addView(views);
    }

    private void startScrollerTask() {
        initialY = getScrollY();
        postDelayed(scrollerTask, DELAY);
    }

    private void initData() {
        long startTime = System.currentTimeMillis();

        displayItemCount = offset * 2 + 1;

        // 2015/12/15 添加此句才可以支持联动效果
        views.removeAllViews();

        for (String item : items) {
            views.addView(createView(item));
        }

        // 2016/1/15 焦点文字颜色高亮位置，逆推“int position = y / itemHeight + offset”
        refreshItemView(itemHeight * (selectedIndex - offset));

        long millis = System.currentTimeMillis() - startTime;
        Log.d(TAG, "init data spent " + millis + "ms");
    }

    private TextView createView(String item) {
        TextView tv = new TextView(context);
        tv.setLayoutParams(new LayoutParams(MATCH_PARENT, WRAP_CONTENT));
        tv.setSingleLine(true);
        tv.setEllipsize(TextUtils.TruncateAt.END);
        tv.setText(item);
        tv.setTextSize(textSize);
        tv.setGravity(Gravity.CENTER);
        int paddingTopBottom = toPx(TEXT_PADDING_TOP_BOTTOM);
        int paddingLeftRight = toPx( TEXT_PADDING_LEFT_RIGHT);
        tv.setPadding(paddingLeftRight, paddingTopBottom, paddingLeftRight, paddingTopBottom);
        if (0 == itemHeight) {
            int wSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
            int hSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
            tv.measure(wSpec, hSpec);
            itemHeight = tv.getMeasuredHeight();
            Log.d(TAG, "itemHeight: " + itemHeight);
            views.setLayoutParams(new LayoutParams(MATCH_PARENT, itemHeight * displayItemCount));
            LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) getLayoutParams();
            setLayoutParams(new LinearLayout.LayoutParams(lp.width, itemHeight * displayItemCount));
        }
        return tv;
    }

    private static int toPx(float dpValue) {
        Resources resources = Resources.getSystem();
        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpValue, resources.getDisplayMetrics());
        return (int) px;
    }

    private void refreshItemView(int y) {
        int position = y / itemHeight + offset;
        int remainder = y % itemHeight;
        int divided = y / itemHeight;

        if (remainder == 0) {
            position = divided + offset;
        } else {
            if (remainder > itemHeight / 2) {
                position = divided + offset + 1;
            }
        }
        Log.d(TAG,"current scroll position : " + position);

        int childSize = views.getChildCount();
        for (int i = 0; i < childSize; i++) {
            TextView itemView = (TextView) views.getChildAt(i);
            if (null == itemView) {
                return;
            }
            // 2015/12/15 可设置颜色
            if (position == i) {
                itemView.setTextColor(textColorFocus);
            } else {
                itemView.setTextColor(textColorNormal);
            }
        }
    }

    /**
     * 选中回调
     */
    private void onSelectedCallBack() {
        if (null != onWheelListener) {
            // 2015/12/25 真实的index应该忽略偏移量
            int realIndex = selectedIndex - offset;
            Log.d(TAG,"isUserScroll=" + isUserScroll + ",selectedIndex=" + selectedIndex + ",realIndex=" + realIndex);
            onWheelListener.onSelected(isUserScroll, realIndex, items.get(this.selectedIndex));
        }
    }

    @Deprecated
    @Override
    public final void setBackgroundColor(int color) {
        throwUnsupportedException();
    }

    @Deprecated
    @Override
    public final void setBackgroundResource(int resid) {
        throwUnsupportedException();
    }

    @Deprecated
    @Override
    public final void setBackground(Drawable background) {
        throwUnsupportedException();
    }

    @Deprecated
    @SuppressWarnings("deprecation")
    @Override
    public final void setBackgroundDrawable(Drawable background) {
        throwUnsupportedException();
    }

    private void throwUnsupportedException() {
        throw new UnsupportedOperationException("don't set background");
    }

    private void changeBackgroundLineDrawable(boolean isSizeChanged) {
        Log.d(TAG, "isSizeChanged=" + isSizeChanged + ", config is " + lineConfig);
        //noinspection deprecation
        super.setBackgroundDrawable(new LineDrawable(lineConfig));
    }

    public void setLineConfig(@Nullable LineConfig config) {
        if (null == config) {
            Log.d(TAG, "line config is null");
            return;
        }
        if (null == lineConfig) {
            config.setWidth(getResources().getDisplayMetrics().widthPixels);
            int[] area = new int[2];
            area[0] = itemHeight * offset;
            area[1] = itemHeight * (offset + 1);
            config.setArea(area);
        }
        lineConfig = config;
        changeBackgroundLineDrawable(false);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        Log.d(TAG, "horizontal scroll origin: " + l + ", vertical scroll origin: " + t);
        refreshItemView(t);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        Log.d(TAG, "onSizeChanged viewWidth=" + w);
        if (null == lineConfig) {
            lineConfig = new LineConfig();
            lineConfig.setColor(LINE_COLOR);
            lineConfig.setWidth(w);
            lineConfig.setThick(toPx( LINE_THICK));
        }
        lineConfig.setWidth(w);
        int[] area = new int[2];
        area[0] = itemHeight * offset;
        area[1] = itemHeight * (offset + 1);
        lineConfig.setArea(area);
        changeBackgroundLineDrawable(true);
    }

    @Override
    public void fling(int velocityY) {
        super.fling(velocityY / 3);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        isUserScroll = true;//触发触摸事件，说明是用户在滚动
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                previousY = ev.getY();
                break;
            case MotionEvent.ACTION_UP:
                Log.d(TAG, String.format("items=%s, offset=%s", items.size(), offset));
                Log.d(TAG,"selectedIndex=" + selectedIndex);
                if (cycleDisable) {
                    startScrollerTask();
                    break;
                }
                float delta = ev.getY() - previousY;
                Log.d(TAG, "delta=" + delta);
                if (selectedIndex == offset && delta > 0) {
                    //滑动到第一项时，若继续向下滑动，则自动跳到最后一项
                    setSelectedIndex(items.size() - offset * 2 - 1);
                } else if (selectedIndex == (items.size() - offset - 1) && delta < 0) {
                    //滑动到最后一项时，若继续向上滑动，则自动跳到第一项
                    setSelectedIndex(0);
                } else {
                    startScrollerTask();
                }
                break;
        }
        return super.onTouchEvent(ev);
    }

    private void _setItems(List<String> list) {
        items.clear();
        items.addAll(list);
        // 前面和后面补全
        for (int i = 0; i < offset; i++) {
            items.addFirst("");
            items.addLast("");
        }
        initData();
    }

    public void setItems(List<String> list) {
        _setItems(list);
        // 2015/12/25 初始化时设置默认选中项
        setSelectedIndex(0);
    }

    public void setItems(String[] list) {
        setItems(Arrays.asList(list));
    }

    public void setItems(List<String> list, int index) {
        _setItems(list);
        setSelectedIndex(index);
    }

    public void setItems(List<String> list, String item) {
        _setItems(list);
        setSelectedItem(item);
    }

    public void setItems(String[] list, int index) {
        setItems(Arrays.asList(list), index);
    }

    public void setItems(String[] list, String item) {
        setItems(Arrays.asList(list), item);
    }

    public int getTextSize() {
        return textSize;
    }

    public void setTextSize(int textSize) {
        this.textSize = textSize;
    }

    public int getTextColor() {
        return textColorFocus;
    }

    public void setTextColor(@ColorInt int textColorNormal, @ColorInt int textColorFocus) {
        this.textColorNormal = textColorNormal;
        this.textColorFocus = textColorFocus;
    }

    public void setTextColor(@ColorInt int textColor) {
        this.textColorFocus = textColor;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(@IntRange(from = 1, to = 3) int offset) {
        if (offset < 1 || offset > 3) {
            throw new IllegalArgumentException("Offset must between 1 and 3");
        }
        this.offset = offset;
    }

    public void setCycleDisable(boolean cycleDisable) {
        this.cycleDisable = cycleDisable;
    }

    /**
     * 从0开始计数，所有项包括偏移量
     */
    public void setSelectedIndex(@IntRange(from = 0) final int index) {
        isUserScroll = false;
        this.post(new Runnable() {
            @Override
            public void run() {
                //滚动到选中项的位置，smoothScrollTo滚动视觉效果有延迟
                //smoothScrollTo(0, index * itemHeight);
                scrollTo(0, index * itemHeight);
                //选中这一项的值
                selectedIndex = index + offset;
                onSelectedCallBack();
                //默认选中第一项时颜色需要高亮
                refreshItemView(itemHeight * index);
            }
        });
    }

    public void setSelectedItem(String item) {
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).equals(item)) {
                //调用_setItems(List)时额外添加了offset个占位符到items里，需要忽略占位符所占的位
                setSelectedIndex(i - offset);
                break;
            }
        }
    }

    public String getSelectedItem() {
        return items.get(selectedIndex);
    }

    public int getSelectedIndex() {
        return selectedIndex - offset;
    }

    /**
     * @deprecated use {@link #setOnWheelListener(OnWheelListener)} instead
     */
    @Deprecated
    public void setOnWheelViewListener(OnWheelViewListener onWheelListener) {
        setOnWheelListener(onWheelListener);
    }

    public void setOnWheelListener(OnWheelListener onWheelListener) {
        this.onWheelListener = onWheelListener;
    }

    public interface OnWheelListener {
        /**
         * 滑动选择回调
         *
         * @param isUserScroll 是否用户手动滚动，用于联动效果判断是否自动重置选中项
         * @param index        当前选择项的索引
         * @param item         当前选择项的值
         */
        void onSelected(boolean isUserScroll, int index, String item);
    }

    /**
     * @deprecated use {@link OnWheelListener} instead
     */
    @Deprecated
    public interface OnWheelViewListener extends OnWheelListener {
    }

    private class ScrollerTask implements Runnable {

        @Override
        public void run() {
            // 2015/12/17 java.lang.ArithmeticException: divide by zero
            if (itemHeight == 0) {
                Log.d(TAG, "itemHeight is zero");
                return;
            }
            int newY = getScrollY();
            if (initialY - newY != 0) {
                startScrollerTask();
                return;
            }
            // stopped
            final int remainder = initialY % itemHeight;
            final int divided = initialY / itemHeight;
            Log.d(TAG, "initialY: " + initialY + ", remainder: " + remainder + ", divided: " + divided);
            if (remainder == 0) {
                selectedIndex = divided + offset;
                onSelectedCallBack();
                return;
            }
            if (remainder > itemHeight / 2) {
                post(new Runnable() {
                    @Override
                    public void run() {
                        smoothScrollTo(0, initialY - remainder + itemHeight);
                        selectedIndex = divided + offset + 1;
                        onSelectedCallBack();
                    }
                });
            } else {
                post(new Runnable() {
                    @Override
                    public void run() {
                        smoothScrollTo(0, initialY - remainder);
                        selectedIndex = divided + offset;
                        onSelectedCallBack();
                    }
                });
            }
        }

    }

    /**
     * 选中项的分割线
     */
    public static class LineConfig {
        private int color = LINE_COLOR;
        private int alpha = LINE_ALPHA;
        private float ratio = (float) (1.0 / 6.0);
        private float thick = -1;// dp
        private int width = 0;
        private int[] area = null;

        public LineConfig() {
            super();
        }

        public LineConfig(@FloatRange(from = 0, to = 1) float ratio) {
            this.ratio = ratio;
        }

        @ColorInt
        public int getColor() {
            return color;
        }

        /**
         * 线颜色
         */
        public void setColor(@ColorInt int color) {
            this.color = color;
        }

        @IntRange(from = 1, to = 255)
        public int getAlpha() {
            return alpha;
        }

        /**
         * 线透明度
         */
        public void setAlpha(@IntRange(from = 1, to = 255) int alpha) {
            this.alpha = alpha;
        }

        @FloatRange(from = 0, to = 1)
        public float getRatio() {
            return ratio;
        }

        /**
         * 线比例，范围为0-1,0表示最长，1表示最短
         */
        public void setRatio(@FloatRange(from = 0, to = 1) float ratio) {
            this.ratio = ratio;
        }

        public float getThick() {
            if (thick == -1) {
                thick = toPx(LINE_THICK);
            }
            return thick;
        }

        /**
         * 线粗
         */
        public void setThick(float thick) {
            this.thick = thick;
        }

        protected int getWidth() {
            return width;
        }

        protected void setWidth(int width) {
            this.width = width;
        }

        @Size(2)
        protected int[] getArea() {
            return area;
        }

        protected void setArea(@Size(2) int[] area) {
            this.area = area;
        }

        @Override
        public String toString() {
            return "color=" + color + ", alpha=" + alpha + ", thick=" + thick + ", width=" + width;
        }

    }

    private static class LineDrawable extends Drawable {
        private Paint paint;
        private LineConfig config;

        LineDrawable(LineConfig cfg) {
            this.config = cfg;
            paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            paint.setColor(config.getColor());
            paint.setAlpha(config.getAlpha());
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(config.getThick());
        }

        @Override
        public void draw(@NonNull Canvas canvas) {
            int[] area = config.getArea();
            int width = config.getWidth();
            float ratio = config.getRatio();
            canvas.drawLine(width * ratio, area[0], width * (1 - ratio), area[0], paint);
            canvas.drawLine(width * ratio, area[1], width * (1 - ratio), area[1], paint);
        }

        @Override
        public void setAlpha(int alpha) {

        }

        @Override
        public void setColorFilter(ColorFilter cf) {

        }

        @Override
        public int getOpacity() {
            return PixelFormat.UNKNOWN;
        }
    }
}
