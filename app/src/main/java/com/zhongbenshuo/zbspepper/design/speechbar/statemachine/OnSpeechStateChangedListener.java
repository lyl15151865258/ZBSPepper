package com.zhongbenshuo.zbspepper.design.speechbar.statemachine;

import androidx.annotation.Nullable;

public interface OnSpeechStateChangedListener {
    void onSpeechStateChanged(SpeechBarState state, @Nullable String data);
}
