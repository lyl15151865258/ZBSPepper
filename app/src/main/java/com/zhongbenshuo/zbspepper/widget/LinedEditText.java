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
import android.view.WindowManager;

import static android.content.Context.WINDOW_SERVICE;

@SuppressLint("DrawAllocation")
public class LinedEditText extends AppCompatEditText {

    private Context mContext;
    private Paint mPaint = new Paint();

    public LinedEditText(Context context) {
        super(context);
        mContext = context;
        initPaint();
    }


    public LinedEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initPaint();
    }


    public LinedEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
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
        WindowManager wm = (WindowManager) mContext.getSystemService(WINDOW_SERVICE);
        int windowWidth = wm.getDefaultDisplay().getWidth();
        int windowHeight = wm.getDefaultDisplay().getHeight();
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.BLACK);
        int paddingTop = getPaddingTop();
        int paddingBottom = getPaddingBottom();
        int scrollY = getScrollY();
        int scrollX = getScrollX() + windowWidth;
        int innerHeight = scrollY + getHeight() - paddingBottom;
        int lineHeight = getLineHeight();
        int baseLine = scrollY + (lineHeight - ((scrollY - paddingTop) % lineHeight));
        int x = 8;
        while (baseLine < innerHeight) {
            canvas.drawLine(x, baseLine, scrollX - x, baseLine, paint);
            baseLine += lineHeight;
        }
        super.onDraw(canvas);
    }
}
