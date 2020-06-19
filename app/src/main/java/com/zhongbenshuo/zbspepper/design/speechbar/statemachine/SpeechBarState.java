package com.zhongbenshuo.zbspepper.design.speechbar.statemachine;

public enum SpeechBarState {
    NOT_LISTENING_NO_SOUND,
    NOT_LISTENING_SOUND,

    LISTENING_NO_SOUND,
    LISTENING_SOUND,

    PROCESSING,

    SPEAKING_NOT_UNDERSTOOD,
    SPEAKING_UNDERSTOOD,
}