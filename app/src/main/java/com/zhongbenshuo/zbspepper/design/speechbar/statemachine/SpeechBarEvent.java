package com.zhongbenshuo.zbspepper.design.speechbar.statemachine;

public class SpeechBarEvent {
    private Type type;
    private String data;

    public SpeechBarEvent(Type type) {
        this.type = type;
    }

    public SpeechBarEvent(Type type, String data) {
        this.type = type;
        this.data = data;
    }

    Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public enum Type {
        NOT_LISTENING,
        LISTENING,

        HEARING_STARTED,
        HEARING_STOPPED,

        PROCESSING_STARTED,
        PROCESSING_STOPPED,

        UNDERSTOOD,
        NOT_UNDERSTOOD
    }
}
