package com.zhongbenshuo.zbspepper.design.speechbar;

import android.animation.ValueAnimator;
import android.graphics.drawable.Drawable;
import android.util.AndroidRuntimeException;

import androidx.core.content.ContextCompat;

import com.zhongbenshuo.zbspepper.R;

import java.util.ArrayList;
import java.util.List;

public class BackgroundColorManager {

    private SpeechBarView speechBarView;
    private List<ValueAnimator> disposablesValueAnimators = new ArrayList<>();

    BackgroundColorManager(SpeechBarView speechBarView) {
        this.speechBarView = speechBarView;
    }

    private void switchToColor(Drawable to) {
        speechBarView.setBackground(to);
    }

    public void release() {
        for (ValueAnimator valueAnimator : disposablesValueAnimators) {
            try {
                valueAnimator.end();
            } catch (AndroidRuntimeException ignored) {
            }
        }
        disposablesValueAnimators.clear();
        speechBarView = null;
    }

    public void showNotListening() {
        switchToColor(ContextCompat.getDrawable(speechBarView.getContext(), R.drawable.bg_circle_yellow));
    }

    public void showListening() {
        switchToColor(ContextCompat.getDrawable(speechBarView.getContext(), R.drawable.bg_circle_blue));
    }

}
