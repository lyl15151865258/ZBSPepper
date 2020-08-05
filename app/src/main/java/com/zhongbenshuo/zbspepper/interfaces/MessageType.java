package com.zhongbenshuo.zbspepper.interfaces;

/**
 * 消息内容类别接口
 * Created at 2020/8/4 0004 10:41
 *
 * @author : LiYuliang
 * @version : 2020/8/4 0004 10:41
 */

public interface MessageType {

    int TEXT = 101;
    int PICTURE = 102;
    int AUDIO = 103;
    int VIDEO = 104;
    int MAP = 105;
    int HTML = 106;
    int FILE = 107;

    int getType();

    // 消息时间
    long getTime();

}