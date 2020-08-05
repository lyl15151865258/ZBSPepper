package com.zhongbenshuo.zbspepper.bean;

import com.zhongbenshuo.zbspepper.interfaces.MessageType;

/**
 * 聊天文本实体类
 * Created at 2020/1/7 0007 23:32
 *
 * @author : LiYuliang
 * @version : 2020/1/7 0007 23:32
 */

public class ChatText implements MessageType {

    @Override
    public int getType() {
        return MessageType.TEXT;
    }

    // 聊天类型
    public enum TEXTTYPE {
        REPLY_CLEAR,
        REPLY_BLURRY,
        LISTEN
    }

    // 对话时间
    private long time;

    // 聊天内容类型
    private TEXTTYPE chatType;

    // 聊天内容文本
    private String chatContent;

    public ChatText(long time, TEXTTYPE chatType, String chatContent) {
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

    public TEXTTYPE getChatType() {
        return chatType;
    }

    public void setChatType(TEXTTYPE chatType) {
        this.chatType = chatType;
    }

    public String getChatContent() {
        return chatContent;
    }

    public void setChatContent(String chatContent) {
        this.chatContent = chatContent;
    }
}
