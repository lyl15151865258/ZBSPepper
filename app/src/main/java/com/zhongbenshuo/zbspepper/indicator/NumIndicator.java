package com.zhongbenshuo.zbspepper.indicator;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;

import com.youth.banner.indicator.BaseIndicator;
import com.youth.banner.util.BannerUtils;

/**
 * 自定义数字指示器demo
 * Created at 2020/7/6 0006 15:25
 *
 * @author : LiYuliang
 * @version : 2020/7/6 0006 15:25
 */

public class NumIndicator extends BaseIndicator {
    private int width;
    private int height;
    private int radius;

    public NumIndicator(Context context) {
        this(context, null);
    }

    public NumIndicator(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NumIndicator(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mPaint.setTextSize(BannerUtils.dp2px(14));
        mPaint.setTextAlign(Paint.Align.CENTER);
        width = (int) BannerUtils.dp2px(44);
        height = (int) BannerUtils.dp2px(44);
        radius = (int) BannerUtils.dp2px(22);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int count = config.getIndicatorSize();
        if (count <= 1) {
            return;
        }
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int count = config.getIndicatorSize();
        if (count <= 1) {
            return;
        }

        RectF rectF = new RectF(0, 0, width, height);
        mPaint.setColor(Color.parseColor("#70000000"));
        canvas.drawRoundRect(rectF, radius, radius, mPaint);

        String text = config.getCurrentPosition() + 1 + "/" + count;
        mPaint.setColor(Color.WHITE);
        canvas.drawText(text, width / 2, (float) (height * 0.62), mPaint);

    }

}
