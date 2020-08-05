package com.zhongbenshuo.zbspepper.bean;

import android.graphics.Bitmap;

import com.zhongbenshuo.zbspepper.interfaces.MessageType;

/**
 * 聊天图片实体类
 * Created at 2020/1/7 0007 23:32
 *
 * @author : LiYuliang
 * @version : 2020/1/7 0007 23:32
 */

public class ChatPicture implements MessageType {

    // 对话时间
    private long time;
    // 图片资源类型
    private PICTURETYPE chatType;
    // 文本描述
    private String text;
    // 图片资源
    private String url;
    // 图片资源
    private int resource;
    // 图片资源
    private Bitmap bitmap;

    public ChatPicture(long time, PICTURETYPE chatType, String text, String url, int resource, Bitmap bitmap) {
        this.time = time;
        this.chatType = chatType;
        this.text = text;
        this.url = url;
        this.resource = resource;
        this.bitmap = bitmap;
    }

    @Override
    public int getType() {
        return MessageType.PICTURE;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public PICTURETYPE getChatType() {
        return chatType;
    }

    public void setChatType(PICTURETYPE chatType) {
        this.chatType = chatType;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getResource() {
        return resource;
    }

    public void setResource(int resource) {
        this.resource = resource;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    // 图片类型
    public enum PICTURETYPE {
        URL,
        BITMAP,
        RESOURCE
    }
}
