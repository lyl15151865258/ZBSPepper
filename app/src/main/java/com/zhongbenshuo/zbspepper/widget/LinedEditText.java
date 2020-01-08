package com.zhongbenshuo.zbspepper.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.PathEffect;
import android.support.v7.widget.AppCompatEditText;
import android.util.AttributeSet;

import com.zhongbenshuo.zbspepper.utils.LogUtils;

@SuppressLint("DrawAllocation")
public class LinedEditText extends AppCompatEditText {

    private Paint mPaint = new Paint();

    public LinedEditText(Context context) {
        super(context);
        initPaint();
    }


    public LinedEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        initPaint();
    }


    public LinedEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initPaint();
    }


    private void initPaint() {
        // 设置画笔颜色
//       mPaint.setColor(0x80000000);
        mPaint.setColor(Color.DKGRAY);
        mPaint.setStrokeWidth(5f);
        // 设置画直线格式
        mPaint.setStyle(Paint.Style.STROKE);
        // 设置虚线效果
        PathEffect effects = new DashPathEffect(new float[]{10, 10, 10, 10}, 10);
        mPaint.setPathEffect(effects);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        int left = getLeft();
        int right = getRight();
        int paddingTop = getPaddingTop();
        int paddingBottom = getPaddingBottom();
        int paddingLeft = getPaddingLeft();
        int paddingRight = getPaddingRight();
        int height = getHeight();
        int lineHeight = getLineHeight();
        float spacingHeight = getLineSpacingExtra();
        int count = (height - paddingTop - paddingBottom) / (int) (lineHeight * getLineSpacingMultiplier());
        LogUtils.d("带虚线输入框", "left：" + left + ",right：" + right + ",paddingTop：" + paddingTop +
                ",paddingBottom：" + paddingBottom + ",paddingLeft：" + paddingLeft + ",paddingRight：" + paddingRight +
                ",height：" + height + ",lineHeight：" + lineHeight + ",spacingHeight：" + spacingHeight);

        for (int i = 0; i < count; i++) {
            float baseline = lineHeight * getLineSpacingMultiplier() * (i + 1) + paddingTop - spacingHeight / 2;
            canvas.drawLine(left + paddingLeft, baseline, right - paddingRight, baseline, mPaint);

            LogUtils.d("带虚线输入框", "startX：" + (left + paddingLeft) + ",startY：" + baseline +
                    ",stopX：" + (right - paddingRight) + ",stopY：" + baseline + ",baseline：" + baseline);
        }

        super.onDraw(canvas);
    }
}
