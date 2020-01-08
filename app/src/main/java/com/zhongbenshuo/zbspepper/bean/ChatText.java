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

    public ChatText(CHATTYPE chatType, String chatContent) {
        this.chatType = chatType;
        this.chatContent = chatContent;
    }

    // 聊天内容类型
    private CHATTYPE chatType;

    // 聊天内容文本
    private String chatContent;

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
