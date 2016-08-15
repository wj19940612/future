package com.jnhyxx.html5.view.market;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;

public class TestView extends View {

    public static Paint sPaint;
    private Path mPath;


    public TestView(Context context) {
        super(context);
        init();
    }

    public TestView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        sPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        sPaint.setColor(Color.RED);
        sPaint.setStyle(Paint.Style.STROKE);
        sPaint.setStrokeWidth(2);
        LinearGradient gradient = new LinearGradient(0, 0, 0, getHeight(), Color.WHITE,
                Color.BLACK, Shader.TileMode.CLAMP);
        sPaint.setShader(gradient);
        mPath = new Path();
    }

    @Override
    protected void onDraw(Canvas canvas) {

        mPath.moveTo(0, 0);
        mPath.lineTo(100, 100);
        mPath.lineTo(200, 100);
        mPath.lineTo(300, 200);
        canvas.drawPath(mPath, sPaint);
    }
}
