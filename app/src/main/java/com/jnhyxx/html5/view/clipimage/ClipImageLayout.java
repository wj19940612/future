package com.jnhyxx.html5.view.clipimage;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.widget.RelativeLayout;

import com.squareup.picasso.Picasso;

import java.io.File;

/**
 * http://blog.csdn.net/lmj623565791/article/details/39761281
 *
 * @author zhy
 */
public class ClipImageLayout extends RelativeLayout {

    private static final String TAG = "ClipImageLayout";
    private int mWidthPixels;
    private int mHeightPixels;
    private ClipZoomImageView mZoomImageView;
    private ClipImageBorderView mClipImageView;

    /**
     * 这里测试，直接写死了大小，真正使用过程中，可以提取为自定义属性
     */
    private int mHorizontalPadding = 20;

    public ClipImageLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        mZoomImageView = new ClipZoomImageView(context);
        mClipImageView = new ClipImageBorderView(context);

        android.view.ViewGroup.LayoutParams lp = new LayoutParams(
                android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                android.view.ViewGroup.LayoutParams.MATCH_PARENT);

        /**
         * 这里测试，直接写死了图片，真正使用过程中，可以提取为自定义属性
         */
//		mZoomImageView.setImageDrawable(getResources().getDrawable(
//				R.drawable.a));

        this.addView(mZoomImageView, lp);
        this.addView(mClipImageView, lp);


        // 计算padding的px
        mHorizontalPadding = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, mHorizontalPadding, getResources()
                        .getDisplayMetrics());
        mZoomImageView.setHorizontalPadding(mHorizontalPadding);
        mClipImageView.setHorizontalPadding(mHorizontalPadding);

        DisplayMetrics dm = new DisplayMetrics();
        ((Activity) getContext()).getWindowManager().getDefaultDisplay().getMetrics(dm);
        mWidthPixels = dm.widthPixels;
        mHeightPixels = dm.heightPixels;
    }

    public void setZoomImageViewImage(Bitmap bitmap) {
        mZoomImageView.setImageBitmap(bitmap);
    }

    public void setZoomImageViewImage(String bitmapUrl) {
        int width = getImageWidthHeight(bitmapUrl)[0];
        int height = getImageWidthHeight(bitmapUrl)[1];

        Log.d(TAG, "测量 宽度 " + width + " 高度 " + height);
        if (width != 0 && height != 0
                && width > height
                && width < mHeightPixels) {
            File file = new File(bitmapUrl);
            Picasso.with(getContext()).load(file)
                    .resize(width, height)
                    .into(mZoomImageView);
        } else if (width != 0 && height != 0
                && width < height
                && height < mHeightPixels) {
            File file = new File(bitmapUrl);
            Picasso.with(getContext()).load(file)
                    .resize(width, height)
                    .into(mZoomImageView);
        } else {
            File file = new File(bitmapUrl);
            Picasso.with(getContext()).load(file)
                    .resize(mWidthPixels - 2 * mHorizontalPadding, mHeightPixels - 2 * mHorizontalPadding)
                    .into(mZoomImageView);
        }
    }

    public static int[] getImageWidthHeight(String path) {
        BitmapFactory.Options options = new BitmapFactory.Options();

        /**
         * 最关键在此，把options.inJustDecodeBounds = true;
         * 这里再decodeFile()，返回的bitmap为空，但此时调用options.outHeight时，已经包含了图片的高了
         */
        options.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(path, options); // 此时返回的bitmap为null
        /**
         *options.outHeight为原始图片的高
         */
        return new int[]{options.outWidth, options.outHeight};
    }

    /**
     * 对外公布设置边距的方法,单位为dp
     *
     * @param mHorizontalPadding
     */
    public void setHorizontalPadding(int mHorizontalPadding) {
        this.mHorizontalPadding = mHorizontalPadding;
    }

    /**
     * 裁切图片
     *
     * @return
     */
    public Bitmap clip() {
        return mZoomImageView.clip();
    }

}
