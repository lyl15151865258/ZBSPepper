package com.zhongbenshuo.zbspepper.bean;

/**
 * 聊天文本实体类
 * Created at 2020/1/7 0007 23:32
 *
 * @author : LiYuliang
 * @version : 2020/1/7 0007 23:32
 */

public class ChatText {

    // 聊天类型
    public enum CHATTYPE {
        REPLY_CLEAR,
        REPLY_BLURRY,
        LISTEN
    }

    // 对话时间
    private long time;

    // 聊天内容类型
    private CHATTYPE chatType;

    // 聊天内容文本
    private String chatContent;

    public ChatText(long time, CHATTYPE chatType, String chatContent) {
        this.time = time;
        this.chatType = chatType;
        this.chatContent = chatContent;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public CHATTYPE getChatType() {
        return chatType;
    }

    public void setChatType(CHATTYPE chatType) {
        this.chatType = chatType;
    }

    public String getChatContent() {
        return chatContent;
    }

    public void setChatContent(String chatContent) {
        this.chatContent = chatContent;
    }
}
