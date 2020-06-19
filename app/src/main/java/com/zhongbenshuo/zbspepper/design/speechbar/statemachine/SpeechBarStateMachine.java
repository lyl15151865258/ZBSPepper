package com.zhongbenshuo.zbspepper.design.speechbar.statemachine;

public class SpeechBarStateMachine {
    private SpeechBarState state = SpeechBarState.NOT_LISTENING_NO_SOUND;

    private boolean isListening;
    private boolean isUnderstood;
    private boolean isNotUnderstood;
    private boolean isHearing;
    private boolean isProcessing;

    private OnSpeechStateChangedListener onSpeechStateChangedListener;

    public void push(SpeechBarEvent speechBarEvent) {
        switch (speechBarEvent.getType()) {
            case NOT_LISTENING:
                isListening = false;
                if (isHearing) {
                    onNext(SpeechBarState.NOT_LISTENING_SOUND);
                } else {
                    onNext(SpeechBarState.NOT_LISTENING_NO_SOUND);
                }
                break;

            case LISTENING:
                isListening = true;
                if (isHearing) {
                    onNext(SpeechBarState.LISTENING_SOUND);
                } else {
                    onNext(SpeechBarState.LISTENING_NO_SOUND);
                }
                break;

            case HEARING_STARTED:
                isHearing = true;
                if (isListening) {
                    onNext(SpeechBarState.LISTENING_SOUND);
                } else {
                    onNext(SpeechBarState.NOT_LISTENING_SOUND);
                }
                break;

            case HEARING_STOPPED:
                isHearing = false;
                if (isListening) {
                    onNext(SpeechBarState.LISTENING_NO_SOUND);
                } else {
                    onNext(SpeechBarState.NOT_LISTENING_NO_SOUND);
                }
                break;

            case PROCESSING_STARTED:
                isProcessing = true;
                onNext(SpeechBarState.PROCESSING);
                break;

            case PROCESSING_STOPPED:
                isProcessing = false;
                if (isUnderstood) {
                    onNext(SpeechBarState.SPEAKING_UNDERSTOOD);
                } else if (isNotUnderstood) {
                    onNext(SpeechBarState.SPEAKING_NOT_UNDERSTOOD);
                }
                break;

            case UNDERSTOOD:
                isUnderstood = true;
                onNext(SpeechBarState.SPEAKING_UNDERSTOOD, speechBarEvent.getData());
                break;

            case NOT_UNDERSTOOD:
                isNotUnderstood = true;
                onNext(SpeechBarState.SPEAKING_NOT_UNDERSTOOD);
                break;
        }
    }

    private void onNext(SpeechBarState newState, String data) {
        if (state == newState) {
            return;
        }

        this.state = newState;

        if (onSpeechStateChangedListener != null) {
            onSpeechStateChangedListener.onSpeechStateChanged(newState, data);
        }
    }

    private void onNext(SpeechBarState newState) {
        onNext(newState, null);
    }

    public void setOnStateChangedListener(OnSpeechStateChangedListener onStateChangedListener) {
        this.onSpeechStateChangedListener = onStateChangedListener;
    }
}
