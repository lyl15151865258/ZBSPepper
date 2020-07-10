package com.zhongbenshuo.zbspepper.design.speechbar;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import com.aldebaran.qi.sdk.object.conversation.Phrase;
import com.zhongbenshuo.zbspepper.R;
import com.zhongbenshuo.zbspepper.design.speechbar.statemachine.SpeechBarEvent;
import com.zhongbenshuo.zbspepper.design.speechbar.statemachine.SpeechBarState;
import com.zhongbenshuo.zbspepper.design.speechbar.statemachine.SpeechBarStateMachine;

import static com.zhongbenshuo.zbspepper.design.speechbar.statemachine.SpeechBarEvent.Type.HEARING_STARTED;
import static com.zhongbenshuo.zbspepper.design.speechbar.statemachine.SpeechBarEvent.Type.HEARING_STOPPED;
import static com.zhongbenshuo.zbspepper.design.speechbar.statemachine.SpeechBarEvent.Type.LISTENING;
import static com.zhongbenshuo.zbspepper.design.speechbar.statemachine.SpeechBarEvent.Type.NOT_LISTENING;
import static com.zhongbenshuo.zbspepper.design.speechbar.statemachine.SpeechBarEvent.Type.NOT_UNDERSTOOD;
import static com.zhongbenshuo.zbspepper.design.speechbar.statemachine.SpeechBarEvent.Type.PROCESSING_STARTED;
import static com.zhongbenshuo.zbspepper.design.speechbar.statemachine.SpeechBarEvent.Type.PROCESSING_STOPPED;
import static com.zhongbenshuo.zbspepper.design.speechbar.statemachine.SpeechBarEvent.Type.UNDERSTOOD;
import static com.zhongbenshuo.zbspepper.design.speechbar.statemachine.SpeechBarState.NOT_LISTENING_NO_SOUND;

public class SpeechBarView extends FrameLayout implements ISpeechBarView {

    private final SpeechBarStateMachine speechBarStateMachine = new SpeechBarStateMachine();
    private TextAnimationManager textAnimationManager;
    private SpeechAnimationManager speechAnimationManager;
    private BackgroundColorManager backgroundDrawableManager;

    public SpeechBarView(Context context) {
        super(context);

        init();
    }

    public SpeechBarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SpeechBarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @Override
    protected void onDetachedFromWindow() {
        speechBarStateMachine.setOnStateChangedListener(null);

        speechAnimationManager.release();
        backgroundDrawableManager.release();
        textAnimationManager.release();

        super.onDetachedFromWindow();
    }

    private void init() {
        inflate(getContext(), R.layout.view_speech_bar, this);

        if (isInEditMode()) {
            return;
        }

        textAnimationManager = new TextAnimationManager(findViewById(R.id.speech));
        speechAnimationManager = new SpeechAnimationManager(findViewById(R.id.speech_animation));
        backgroundDrawableManager = new BackgroundColorManager(this);

        speechBarStateMachine.setOnStateChangedListener(this::onSpeechStateChanged);

        backgroundDrawableManager.showNotListening();
        textAnimationManager.clearText();

        onSpeechStateChanged(NOT_LISTENING_NO_SOUND, null);
    }

    @Override
    public void clearHeardText() {
        textAnimationManager.clearText();
    }

    @Override
    public void showListening(boolean listening) {
        if (listening) {
            speechBarStateMachine.push(new SpeechBarEvent(LISTENING));
        } else {
            speechBarStateMachine.push(new SpeechBarEvent(NOT_LISTENING));
        }
    }

    @Override
    public void showHearing(boolean hearing) {
        if (hearing) {
            speechBarStateMachine.push(new SpeechBarEvent(HEARING_STARTED));
        } else {
            speechBarStateMachine.push(new SpeechBarEvent(HEARING_STOPPED));
        }
    }

    @Override
    public void showHeardText(String text) {
        speechBarStateMachine.push(new SpeechBarEvent(UNDERSTOOD, text));
    }

    @Override
    public void showOnFallbackReplyFoundFor(Phrase phrase) {
        speechBarStateMachine.push(new SpeechBarEvent(UNDERSTOOD, phrase.getText()));
    }

    @Override
    public void onNoReplyFoundFor(Phrase phrase) {
        speechBarStateMachine.push(new SpeechBarEvent(UNDERSTOOD, phrase.getText()));
    }

    @Override
    public void showOnNoPhraseRecognized() {
        showNotUnderstood();
    }

    @Override
    public void showProcessing(boolean processing) {
        if (processing) {
            speechBarStateMachine.push(new SpeechBarEvent(PROCESSING_STARTED));
        } else {
            speechBarStateMachine.push(new SpeechBarEvent(PROCESSING_STOPPED));
        }
    }

    //endregion

    private void showNotUnderstood() {
        speechBarStateMachine.push(new SpeechBarEvent(NOT_UNDERSTOOD));
    }

    private void onSpeechStateChanged(SpeechBarState state, String data) {
        switch (state) {
            case NOT_LISTENING_NO_SOUND:
            case NOT_LISTENING_SOUND:
                backgroundDrawableManager.showNotListening();
                speechAnimationManager.hideHumanSpeaking();
                break;
            case LISTENING_NO_SOUND:
                backgroundDrawableManager.showListening();
                speechAnimationManager.hideHumanSpeaking();
                textAnimationManager.clearText();
                break;
            case LISTENING_SOUND:
                backgroundDrawableManager.showListening();
                speechAnimationManager.showHumanSpeaking();
                textAnimationManager.clearText();
                break;
            case PROCESSING:
                backgroundDrawableManager.showNotListening();
                speechAnimationManager.hideHumanSpeaking();
                textAnimationManager.clearText();
                break;
            case SPEAKING_NOT_UNDERSTOOD:
                speechAnimationManager.hideHumanSpeaking();
                textAnimationManager.displayText("?");
                break;
            case SPEAKING_UNDERSTOOD:
                speechAnimationManager.hideHumanSpeaking();
//                textAnimationManager.displayText(data);
                break;
        }
    }
}
