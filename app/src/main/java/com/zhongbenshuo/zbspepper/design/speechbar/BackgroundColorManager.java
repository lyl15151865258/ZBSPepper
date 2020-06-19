package com.zhongbenshuo.zbspepper.design.speechbar;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.content.ContextCompat;
import android.util.AndroidRuntimeException;

import com.zhongbenshuo.zbspepper.R;

import java.util.ArrayList;
import java.util.List;

public class BackgroundColorManager {

    private static final long SMALL_ANIMATION_DURATION = 120;

    private SpeechBarView speechBarView;
    private List<ValueAnimator> disposablesValueAnimators = new ArrayList<>();

    BackgroundColorManager(SpeechBarView speechBarView) {
        this.speechBarView = speechBarView;
    }

    private void switchToColor(int to) {
        switchToColor(to, SMALL_ANIMATION_DURATION);
    }

    private void switchToColor(int to, long duration) {
        int from = Color.TRANSPARENT;

        ColorDrawable colorFrom = (ColorDrawable) speechBarView.getBackground();
        if (colorFrom != null) {
            from = colorFrom.getColor();
        }

        ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), from, to);
        disposablesValueAnimators.add(colorAnimation);

        colorAnimation.setDuration(duration);
        colorAnimation.addUpdateListener(animator -> {
            if (speechBarView != null) {
                speechBarView.setBackgroundColor((int) animator.getAnimatedValue());
            }
        });

        colorAnimation.start();
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
        switchToColor(ContextCompat.getColor(speechBarView.getContext(), R.color.speech_bar_not_listening));
    }

    public void showNotListening(int delay) {
        switchToColor(ContextCompat.getColor(speechBarView.getContext(), R.color.speech_bar_not_listening), delay);
    }

    public void showListening() {
        switchToColor(ContextCompat.getColor(speechBarView.getContext(), R.color.speech_bar_listening));
    }
}
