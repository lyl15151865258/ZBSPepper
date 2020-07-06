package com.zhongbenshuo.zbspepper.widget.textview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;

/**
 * Created by zzy on 2018/1/15.
 * Button drawableStart与text水平居中显示
 */

public class DrawableStartCenterButton extends AppCompatButton {

    public DrawableStartCenterButton(Context context) {
        this(context, null);
    }

    public DrawableStartCenterButton(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public void setText(String text) {
        super.setText(text);
        requestLayout();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        float textWidth = getPaint().measureText(getText().toString());
        Drawable[] drawables = getCompoundDrawables();//start,top,end,bottom
        Drawable drawableLeft = drawables[0];
        int totalWidth = getWidth();
        if (drawableLeft != null) {
            int drawableWidth = drawableLeft.getIntrinsicWidth();
            int drawablePadding = getCompoundDrawablePadding();
            float bodyWidth = textWidth + drawableWidth + drawablePadding;
            int padding = (int) (totalWidth - bodyWidth) / 2;
            setPadding(padding, 0, padding, 0);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

}
