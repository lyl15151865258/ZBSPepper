package com.zhongbenshuo.zbspepper.design.speechbar;

import android.os.Handler;
import android.os.Looper;
import android.view.ViewPropertyAnimator;
import android.widget.TextView;

public class TextAnimationManager {

    private static final long SMALL_ANIMATION_DURATION = 120;
    private static final long AUTO_HIDE_DELAY = 8_000;

    private TextView speechTextView;
    private ViewPropertyAnimator textAnimation;
    private Handler uiThread;

    private Runnable hideTextRunnable = () -> displayText("");

    TextAnimationManager(TextView speechTextView) {
        this.speechTextView = speechTextView;
        this.uiThread = new Handler(Looper.getMainLooper());
    }

    public void displayText(String text) {
        displayText(text, SMALL_ANIMATION_DURATION);
    }

    private void displayText(String text, long duration) {
        uiThread.removeCallbacks(hideTextRunnable);

        if (textAnimation != null) {
            textAnimation.cancel();
            textAnimation = null;
        }

        if (text.isEmpty()) {
            textAnimation = speechTextView.animate()
                    .setDuration(duration)
                    .alpha(0)
                    .withEndAction(() -> speechTextView.setText(text));
        } else {
            textAnimation = speechTextView.animate()
                    .setDuration(duration)
                    .alpha(1)
                    .withStartAction(() -> speechTextView.setText(text));
            uiThread.postDelayed(hideTextRunnable, AUTO_HIDE_DELAY);
        }
    }

    public void release() {
    }

    public void clearText() {
        speechTextView.setText("");
    }
}
