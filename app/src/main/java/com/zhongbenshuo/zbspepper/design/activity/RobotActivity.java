package com.zhongbenshuo.zbspepper.design.activity;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.aldebaran.qi.sdk.QiContext;
import com.aldebaran.qi.sdk.QiSDK;
import com.aldebaran.qi.sdk.RobotLifecycleCallbacks;
import com.zhongbenshuo.zbspepper.R;
import com.zhongbenshuo.zbspepper.design.activity.conversationstatus.ConversationStatusBinder;
import com.zhongbenshuo.zbspepper.design.activity.conversationstatus.SpeechBarDisplayStrategy;
import com.zhongbenshuo.zbspepper.design.activity.utils.KeyboardVisibilityWatcher;
import com.zhongbenshuo.zbspepper.design.activity.utils.ScreenFlagsChecker;
import com.zhongbenshuo.zbspepper.design.speechbar.SpeechBarView;

public abstract class RobotActivity extends AppCompatActivity {

    private final ConversationStatusBinder conversationStatusBinder = new ConversationStatusBinder();
    private FrameLayout frameLayout;
    private SpeechBarView speechBarView;
    private final RobotLifecycleCallbacks robotCallbacks = new RobotLifecycleCallbacks() {
        @Override
        public void onRobotFocusGained(QiContext qiContext) {
            conversationStatusBinder.bind(qiContext, speechBarView);
        }

        @Override
        public void onRobotFocusLost() {
            conversationStatusBinder.unbind(false);
        }

        @Override
        public void onRobotFocusRefused(String reason) {
        }
    };
    private ConstraintLayout constraintLayout;
    private KeyboardVisibilityWatcher keyboardVisibilityWatcher = new KeyboardVisibilityWatcher();

    /*
     * Returns the ViewGroup containing the user content.
     */
    protected ViewGroup getMainViewGroup() {
        return frameLayout;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTheme(R.style.RobotTheme);

        ConstraintLayout fullLayout = (ConstraintLayout) getLayoutInflater().inflate(R.layout.activity_robot, null, false);
        constraintLayout = fullLayout.findViewById(R.id.layout);
        speechBarView = fullLayout.findViewById(R.id.speech_bar);
        frameLayout = fullLayout.findViewById(R.id.frame);

        super.setContentView(fullLayout);

        QiSDK.register(this, robotCallbacks);
    }

    @Override
    protected void onResume() {
        super.onResume();

        hideSystemBars();

        conversationStatusBinder.init(constraintLayout, speechBarView);
        keyboardVisibilityWatcher.subscribe(this::hideSystemBars, this);
        speechBarView.clearHeardText();
    }

    @Override
    protected void onPause() {
        keyboardVisibilityWatcher.release();
        conversationStatusBinder.unbind(true);
        super.onPause();
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        hideSystemBars();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        hideSystemBars();
    }

    private void hideSystemBars() {
        if (new ScreenFlagsChecker().hasFlags(getWindow().getDecorView().getSystemUiVisibility(),
                View.SYSTEM_UI_FLAG_FULLSCREEN,
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)) {
            return;
        }

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_IMMERSIVE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    @Override
    public void setContentView(int layoutResID) {
        if (frameLayout.getChildCount() > 0) {
            frameLayout.removeAllViews();
        }

        getLayoutInflater().inflate(layoutResID, frameLayout, true);
    }

    /**
     * Sets the SpeechBar display strategy
     *
     * @param speechBarDisplayStrategy the display strategy to apply
     */
    public void setSpeechBarDisplayStrategy(SpeechBarDisplayStrategy speechBarDisplayStrategy) {
        conversationStatusBinder.setStrategy(speechBarDisplayStrategy);
    }

    @Override
    protected void onDestroy() {
        QiSDK.unregister(this, robotCallbacks);
        conversationStatusBinder.unbind(true);

        super.onDestroy();
    }
}
