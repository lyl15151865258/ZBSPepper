package com.zhongbenshuo.zbspepper.design.speechbar;

import android.graphics.drawable.Drawable;
import android.view.ViewPropertyAnimator;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.vectordrawable.graphics.drawable.Animatable2Compat;
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat;

import com.zhongbenshuo.zbspepper.R;

import java.util.concurrent.atomic.AtomicBoolean;

import static android.view.View.VISIBLE;

public class SpeechAnimationManager {

    private static final long SMALL_ANIMATION_DURATION = 120;
    private static final long MEDIUM_ANIMATION_DURATION = 400;
    private final AtomicBoolean shouldReplaySpeechAnimation = new AtomicBoolean(false);
    private AppCompatImageView speechAnimationView;
    private AnimatedVectorDrawableCompat speechAnimatedVectorDrawable;
    private final Animatable2Compat.AnimationCallback speechAnimationCallback = new Animatable2Compat.AnimationCallback() {
        @Override
        public void onAnimationEnd(Drawable drawable) {
            if (shouldReplaySpeechAnimation.get()) {
                speechAnimationView.post(speechAnimatedVectorDrawable::start);
            }
        }
    };
    private ViewPropertyAnimator speechAnimation;

    SpeechAnimationManager(AppCompatImageView speechAnimationView) {
        this.speechAnimationView = speechAnimationView;

        speechAnimatedVectorDrawable = AnimatedVectorDrawableCompat.create(speechAnimationView.getContext(), R.drawable.speech_bar_voice_animation);
        speechAnimationView.setImageDrawable(speechAnimatedVectorDrawable);
        speechAnimatedVectorDrawable.registerAnimationCallback(this.speechAnimationCallback);
    }

    public void hideHumanSpeaking() {
        if (speechAnimation != null) {
            speechAnimation.cancel();
            speechAnimation = null;
        }
        speechAnimation = speechAnimationView.animate()
                .setDuration(SMALL_ANIMATION_DURATION)
                .alpha(0)
                .withEndAction(() -> {
                    shouldReplaySpeechAnimation.set(false);
                    speechAnimatedVectorDrawable.stop();
                });
        speechAnimation.start();
    }

    public void showHumanSpeaking() {
        if (speechAnimation != null) {
            speechAnimation.cancel();
            speechAnimation = null;
        }

        speechAnimationView.setVisibility(VISIBLE);

        speechAnimation = speechAnimationView.animate()
                .setDuration(MEDIUM_ANIMATION_DURATION)
                .alpha(1)
                .withStartAction(() -> {
                });

        speechAnimatedVectorDrawable.start();
        shouldReplaySpeechAnimation.set(true);

        speechAnimation.start();
    }

    public void release() {
        speechAnimatedVectorDrawable.unregisterAnimationCallback(this.speechAnimationCallback);
    }
}
