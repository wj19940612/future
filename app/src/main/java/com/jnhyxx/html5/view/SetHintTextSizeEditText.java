package com.jnhyxx.html5.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.SpannedString;
import android.text.style.AbsoluteSizeSpan;
import android.util.AttributeSet;
import android.widget.EditText;

import com.jnhyxx.html5.R;

/**
 * Created by ${wangJie} on 2016/9/12.
 * 可以改变hint字体大小的EditText
 * 
 */
// TODO: 2016/9/12 这个目前不能使用
public class SetHintTextSizeEditText extends EditText {


    private int mHintTextSize;

    public SetHintTextSizeEditText(Context context) {
        super(context);

    }

    public SetHintTextSizeEditText(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.SetHintTextSizeEditText);
        int defaultHintTextSize = (int) this.getTextSize();

        mHintTextSize = typedArray.getDimensionPixelOffset(R.styleable.SetHintTextSizeEditText_hintTextSize, defaultHintTextSize);
        CharSequence hintText = this.getHint();
        SpannableString ss = new SpannableString(hintText);//定义hint的值
        AbsoluteSizeSpan ass = new AbsoluteSizeSpan(mHintTextSize, true);//设置字体大小 true表示单位是sp
        ss.setSpan(ass, 0, ss.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        this.setHint(new SpannedString(ss));
        typedArray.recycle();
    }

    public SetHintTextSizeEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
}
