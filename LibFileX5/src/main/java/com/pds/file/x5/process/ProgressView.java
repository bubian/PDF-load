package com.pds.file.x5.process;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

/**
 * @author: pengdaosong
 * @CreateTime: 2020/10/22 6:18 PM
 * @Email: pengdaosong@medlinker.com
 * @Description:
 */

public class ProgressView extends View implements ProgressListener{
    private Paint mPaint;
    private int mWidth;
    private int mHeight;
    private int progress;

    public ProgressView(Context context) {
        this(context, null);
    }

    public ProgressView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ProgressView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        //初始化画笔
        mPaint = new Paint();
        mPaint.setDither(true);
        mPaint.setAntiAlias(true);
        mPaint.setStrokeWidth(10);
        mPaint.setColor(Color.GREEN);
    }

    @Override
    protected void onSizeChanged(int w, int h, int ow, int oh) {
        mWidth = w;
        mHeight = h;
        super.onSizeChanged(w, h, ow, oh);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawRect(0, 0, mWidth * progress / 100, mHeight, mPaint);
        super.onDraw(canvas);
    }

    /**
     * 设置新进度 重新绘制
     *
     * @param newProgress 新进度
     */
    public void setProgress(int newProgress) {
        this.progress = newProgress;
        invalidate();
    }

    /**
     * 设置进度条颜色
     *
     * @param color 色值
     */
    public void setColor(int color) {
        mPaint.setColor(color);
    }

    @Override
    public void onProgress(int i) {
        setProgress(i);
    }

    @Override
    public void onStartDownload() {
        setVisibility(VISIBLE);
    }

    @Override
    public void onComplete() {
        setVisibility(GONE);
    }
}
