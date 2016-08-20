package com.jnhyxx.chart;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;

public abstract class ChartView extends View {

    protected enum ChartColor {

        BASE("#3B4856"),
        TEXT("#A8A8A8"),
        WHITE("#FFFFFF"),
        FILL("#331D3856"),
        BLUE("#358CF3"),
        YELLOW("#EAC281");

        private String value;

        ChartColor(String color) {
            value = color;
        }

        public String get() {
            return value;
        }
    }

    private enum Action {
        NONE,
        DRAG,
        LONG_PRESS,
        ZOOM;
    }

    private static final int FONT_SIZE_DP = 8;
    private static final int FONT_BIG_SIZE_DP = 9;
    private static final int TEXT_MARGIN_WITH_LINE_DP = 5;
    private static final int RECT_PADDING_DP = 4;
    private static final int MIDDLE_EXTRA_SPACE_DP = 10;
    private static final int HEIGHT_TIME_LINE_DP = 24;
    private static final float RATIO_OF_TOP = 0.73f;

    private static final int WHAT_LONG_PRESS = 1;
    private static final int DELAY = 400;
    private static final float CLICK_PIXELS = 2;

    public static Paint sPaint;
    private Path mPath;
    private Path mSecondPath;
    private Paint.FontMetrics mFontMetrics;
    private RectF mRectF;
    private StringBuilder mStringBuilder;
    private Handler mHandler;

    protected ChartSettings mSettings;

    protected float mFontSize;
    protected int mFontHeight;
    protected float mOffset4CenterText; // center y of text + this will draw the text in center you want
    protected float mBigFontSize;
    protected int mBigFontHeight;
    protected float mOffset4CenterBigText;

    protected int mTextMargin; // The margin between text and baseline
    protected int mRectPadding;
    protected int mMiddleExtraSpace; // The middle space between two parts
    private int mTimeLineHeight;
    private int mCenterPartHeight;

    private int mTouchIndex; // The position of cross when touch view
    private float mDownX;
    private float mDownY;

    private float mTransactionX;
    private float mPreviousTransactionX;
    private float mStartX;
    private boolean mDragged;
    private boolean mTouched;
    private Action mAction;

    public ChartView(Context context) {
        super(context);
        init();
    }

    public ChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        sPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPath = new Path();
        mSecondPath = new Path();
        mRectF = new RectF();
        mStringBuilder = new StringBuilder();
        mHandler = new ChartHandler();

        mSettings = new ChartSettings();

        // text font
        mFontSize = sp2Px(FONT_SIZE_DP);
        sPaint.setTextSize(mFontSize);
        mFontMetrics = sPaint.getFontMetrics();
        sPaint.getFontMetrics(mFontMetrics);
        mFontHeight = (int) (mFontMetrics.bottom - mFontMetrics.top);
        mOffset4CenterText = calOffsetY4TextCenter();

        // big text font
        mBigFontSize = sp2Px(FONT_BIG_SIZE_DP);
        sPaint.setTextSize(mBigFontSize);
        sPaint.getFontMetrics(mFontMetrics);
        mBigFontHeight = (int) (mFontMetrics.bottom - mFontMetrics.top);
        mOffset4CenterBigText = calOffsetY4TextCenter();

        // constant
        mTextMargin = (int) dp2Px(TEXT_MARGIN_WITH_LINE_DP);
        mRectPadding = (int) dp2Px(RECT_PADDING_DP);
        mMiddleExtraSpace = (int) dp2Px(MIDDLE_EXTRA_SPACE_DP);
        mTimeLineHeight = (int) dp2Px(HEIGHT_TIME_LINE_DP);
        mCenterPartHeight = mMiddleExtraSpace + mTimeLineHeight;

        // gesture
        mTouchIndex = -1;
        mAction = Action.NONE;
    }

    private class ChartHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            MotionEvent e = (MotionEvent) msg.obj;
            Log.d("TEST", "handleMessage onLongPress: e" + e.toString());
            mAction = Action.LONG_PRESS;
            mTouchIndex = calculateTouchIndex(e);
            redraw();
        }
    }

    public ChartSettings getSettings() {
        return mSettings;
    }

    public void setSettings(ChartSettings settings) {
        mSettings = settings;
        redraw();
    }

    protected void setBaseLinePaint(Paint paint) {
        paint.setColor(Color.parseColor(ChartColor.BASE.get()));
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(1);
    }

    protected void setDefaultTextPaint(Paint paint) {
        paint.setColor(Color.parseColor(ChartColor.TEXT.get()));
        paint.setTextSize(mFontSize);
        paint.setStyle(Paint.Style.FILL);
        paint.setPathEffect(null);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int left = 0 + getPaddingLeft();
        int top = 0 + getPaddingTop();
        int width = getWidth() - getPaddingLeft() - getPaddingRight();
        int topPartHeight = getTopPartHeight();

        calculateBaseLines(mSettings.getBaseLines());

        int top2 = -1;
        if (mSettings.isIndexesEnable()) {
            top2 = top + getTopPartHeight() + mCenterPartHeight;
        }
        drawTitleAboveBaselines(left, top, top2, mTouchIndex, canvas);

        drawBaseLines(mSettings.getBaseLines(), left, top, width, topPartHeight, canvas);

        if (mSettings.isIndexesEnable()) {
            calculateIndexesBaseLines(mSettings.getIndexesBaseLines());

            drawIndexesBaseLines(mSettings.getIndexesBaseLines(),
                    left, top + getTopPartHeight() + mCenterPartHeight,
                    width, getBottomPartHeight(), canvas);
        }

        drawRealTimeData(mSettings.isIndexesEnable(),
                left, top, width, topPartHeight,
                left, top + getTopPartHeight() + mCenterPartHeight, width, getBottomPartHeight(),
                canvas);
        drawTimeLine(left, top + topPartHeight, width, canvas);

        if (mTouchIndex >= 0) {
            drawTopTouchLines(mTouchIndex, left, top, width, topPartHeight, canvas);

            if (mSettings.isIndexesEnable()) {
                drawBottomTouchLines(mTouchIndex, left, top + topPartHeight + mCenterPartHeight,
                        width, getBottomPartHeight(), canvas);
            }

            onTouchLinesAppear(mTouchIndex);
        } else {
            onTouchLinesDisappear();
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (mTouchIndex >= 0) { // touchLines appear
            getParent().requestDisallowInterceptTouchEvent(true);
        } else {
            getParent().requestDisallowInterceptTouchEvent(false);
        }
        return super.dispatchTouchEvent(event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getActionMasked() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                Message message = mHandler.obtainMessage(WHAT_LONG_PRESS, event);
                mHandler.sendMessageDelayed(message, DELAY);

                mDownX = event.getX();
                mDownY = event.getY();
                return true;
            case MotionEvent.ACTION_MOVE:
                if (Math.abs(mDownX - event.getX()) < CLICK_PIXELS
                        || Math.abs(mDownY - event.getY()) < CLICK_PIXELS) {
                    return false;
                }

                mHandler.removeMessages(WHAT_LONG_PRESS);
                if (mAction == Action.LONG_PRESS) {
                    int newTouchIndex = calculateTouchIndex(event);
                    if (newTouchIndex != mTouchIndex) {
                        if (hasThisTouchIndex(newTouchIndex)) {
                            mTouchIndex = newTouchIndex;
                            redraw();
                            return true;
                        }
                    }
                }

                return false;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mHandler.removeMessages(WHAT_LONG_PRESS);
                mAction = Action.NONE;
                mTouchIndex = -1;
                redraw();
                return true;
        }
        return super.onTouchEvent(event);
    }

    protected boolean hasThisTouchIndex(int touchIndex) {
        return false;
    }

    protected int calculateTouchIndex(MotionEvent e) {
        return -1;
    }

    /**
     * draw top touch lines, total area of top without middle
     *
     * @param touchIndex
     * @param left
     * @param top
     * @param width
     * @param height
     * @param canvas
     */
    protected void drawTopTouchLines(int touchIndex, int left, int top, int width, int height,
                                     Canvas canvas) {
    }

    /**
     * draw top touch lines, total area of bottom without middle
     *
     * @param touchIndex
     * @param left
     * @param top
     * @param width
     * @param height
     * @param canvas
     */
    protected void drawBottomTouchLines(int touchIndex, int left, int top, int width, int height,
                                        Canvas canvas) {
    }

    protected void onTouchLinesAppear(int touchIndex) {
    }

    protected void onTouchLinesDisappear() {
    }

    /**
     * the title above content area and indexes area <br/>if indexes is disable, top2 is -1
     *
     * @param left
     * @param top        content draw area top
     * @param top2       indexes draw area top
     * @param touchIndex
     * @param canvas
     */
    protected void drawTitleAboveBaselines(int left, int top, int top2, int touchIndex, Canvas canvas) {

    }

    protected abstract void calculateBaseLines(float[] baselines);

    protected void calculateIndexesBaseLines(float[] indexesBaseLines) {

    }

    /**
     * draw top baselines
     *
     * @param baselines
     * @param left
     * @param top       the first baseline Y axis
     * @param width
     * @param height    the total height of several top baselines without text and textMargin
     * @param canvas
     */
    protected abstract void drawBaseLines(float[] baselines, int left, float top, int width, int height,
                                          Canvas canvas);

    /**
     * draw bottom indexes baselines
     *
     * @param indexesBaseLines
     * @param left
     * @param top              the first baseline Y axis
     * @param width
     * @param height           the total height of several bottom baselines without text and textMargin
     * @param canvas
     */
    protected void drawIndexesBaseLines(float[] indexesBaseLines,
                                        int left, int top, int width, int height,
                                        Canvas canvas) {
    }

    protected abstract void drawRealTimeData(boolean indexesEnable,
                                             int left, int top, int width, int height,
                                             int left2, int top2, int width2, int height2,
                                             Canvas canvas);

    /**
     * draw time line
     *
     * @param left   the left(x) coordinate of time line text
     * @param top    the top(y coordinate）of time line text
     * @param width
     * @param canvas
     */
    protected abstract void drawTimeLine(int left, int top, int width, Canvas canvas);

    private int getTopPartHeight() {
        int originalHeight = getHeight() - getPaddingTop() - getPaddingBottom();
        int topPartHeight = originalHeight - mTimeLineHeight;
        if (mSettings.isIndexesEnable()) {
            return (int) ((topPartHeight - mMiddleExtraSpace) * RATIO_OF_TOP);
        }
        return topPartHeight;
    }

    private int getBottomPartHeight() {
        int originalHeight = getHeight() - getPaddingTop() - getPaddingBottom();
        if (mSettings.isIndexesEnable()) {
            return originalHeight - mCenterPartHeight - getTopPartHeight();
        }
        return 0;
    }

    protected Path getPath() {
        if (mPath == null) {
            mPath = new Path();
        }
        mPath.reset();
        return mPath;
    }

    protected Path getSecondPath() {
        if (mSecondPath == null) {
            mSecondPath = new Path();
        }
        mSecondPath.reset();
        return mSecondPath;
    }

    protected RectF getRectF() {
        if (mRectF == null) {
            mRectF = new RectF();
        }
        return mRectF;
    }

    protected StringBuilder getStringBuilder() {
        if (mStringBuilder == null) {
            mStringBuilder = new StringBuilder();
        }
        mStringBuilder.setLength(0);
        return mStringBuilder;
    }

    protected float getChartY(float y) {
        // When values beyond baselines, eg. mv. return -1
        float[] baselines = mSettings.getBaseLines();
        if (y > baselines[0] || y < baselines[baselines.length - 1]) {
            return -1;
        }

        int height = getTopPartHeight();
        y = (baselines[0] - y) / (baselines[0] - baselines[baselines.length - 1]) * height;
        return y + getPaddingTop();
    }

    protected float getIndexesChartY(float y) {
        // When values beyond indexes baselines, eg. mv. return -1
        float[] indexesBaseLines = mSettings.getIndexesBaseLines();
        if (y > indexesBaseLines[0] || y < indexesBaseLines[indexesBaseLines.length - 1]) {
            return -1;
        }

        int height = getBottomPartHeight() - 2 * (mFontHeight + mTextMargin);
        y = (indexesBaseLines[0] - y) /
                (indexesBaseLines[0] - indexesBaseLines[indexesBaseLines.length - 1]) * height;

        return y + getPaddingTop() + getTopPartHeight() + mCenterPartHeight;
    }

    protected float getChartX(int index) {
        int width = getWidth() - getPaddingLeft() - getPaddingRight();
        return getPaddingLeft() + index * width * 1.0f / mSettings.getXAxis();
    }

    /**
     * this is the inverse operation of getCharX(index)
     *
     * @param x
     * @return
     */
    protected int getIndexOfXAxis(float x) {
        int width = getWidth() - getPaddingLeft() - getPaddingRight();
        x = x - getPaddingLeft();
        return (int) (x * mSettings.getXAxis() / width);
    }

    /**
     * the startXY of drawText is at baseline, this is used to add some offsetY for text center
     * centerY = y + offsetY
     *
     * @return offsetY
     */
    protected float calOffsetY4TextCenter() {
        Paint.FontMetrics fontMetrics = sPaint.getFontMetrics();
        float fontHeight = fontMetrics.bottom - fontMetrics.top;
        return fontHeight / 2 - fontMetrics.bottom;
    }

    /**
     * this method is used to calculate a rectF for the text that will be drew
     * we add some left-right padding for the text, just for nice
     *
     * @param textX     left of text
     * @param textY     y of text baseline
     * @param textWidth
     * @return
     */
    protected RectF getBigFontBgRectF(float textX, float textY, float textWidth) {
        mRectF.left = textX - mRectPadding;
        mRectF.top = textY + mFontMetrics.top;
        mRectF.right = textX + textWidth + mRectPadding;
        mRectF.bottom = textY + mFontMetrics.bottom;
        return mRectF;
    }

    protected float dp2Px(float value) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, getResources().getDisplayMetrics());
    }

    protected float sp2Px(int value) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, value, getResources().getDisplayMetrics());
    }

    protected String formatNumber(float value) {
        return formatNumber(value, mSettings.getNumberScale());
    }

    protected String formatNumber(float value, int numberScale) {
        DecimalFormat decimalFormat = (DecimalFormat) NumberFormat.getInstance();

        String pattern = "##0";
        for (int i = 1; i <= numberScale; i++) {
            if (i == 1) pattern += ".0";
            else pattern += "0";
        }
        decimalFormat.applyPattern(pattern);
        decimalFormat.setRoundingMode(RoundingMode.HALF_EVEN);

        String v = decimalFormat.format(value);
        return v;
    }

    protected void redraw() {
        invalidate(0, 0, getWidth(), getHeight());
    }
}
