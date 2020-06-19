package com.zhongbenshuo.zbspepper.design.activity.utils;

import android.app.Activity;
import android.view.View;
import android.view.ViewTreeObserver;

public class KeyboardVisibilityWatcher implements ViewTreeObserver.OnGlobalLayoutListener {

    private View decorView;
    private OnKeyboardVisibilityChangedListener onKeyboardVisibilityChangedListener;

    public interface OnKeyboardVisibilityChangedListener {
        void onKeyboardChange();
    }

    @Override
    public void onGlobalLayout() {
        if (onKeyboardVisibilityChangedListener != null) {
            onKeyboardVisibilityChangedListener.onKeyboardChange();
        }
    }

    public void subscribe(OnKeyboardVisibilityChangedListener onKeyboardVisibilityChangedListener, Activity activity) {
        this.onKeyboardVisibilityChangedListener = onKeyboardVisibilityChangedListener;

        View decorView = activity.getWindow().getDecorView();
        decorView.setOnSystemUiVisibilityChangeListener(visibility -> {
            if (onKeyboardVisibilityChangedListener != null) {
                onKeyboardVisibilityChangedListener.onKeyboardChange();
            }
        });

    }

    public void release() {
        if (decorView != null) {
            decorView.setOnSystemUiVisibilityChangeListener(null);
            decorView = null;
        }

        onKeyboardVisibilityChangedListener = null;
    }
}