package com.zhongbenshuo.zbspepper.constant;

/**
 * 网络常量值
 * Created at 2018/11/28 13:42
 *
 * @author LiYuliang
 * @version 1.0
 */

public class NetWork {

    public static final String ENCRYPT_KEY = "IZnqNJqgLwPLO9LxMP23xZNmHHq55AmB";

    //主账号IP地址
    public static final String SERVER_DOMAIN_NAME = "www.zhongbenshuo.com";
    public static final String SERVER_HOST_MAIN = "https://www.zhongbenshuo.com";
    //主账号端口号
    public static final String SERVER_PORT_MAIN = "443";
    //主账号项目名
    public static final String PROJECT_MAIN = "OA";

    //http请求超时时间（单位：秒）
    public static final int TIME_OUT_HTTP = 5;

    //WebSocket端口
    public static final String PORT_WEBSOCKET = "9010";
    //WebSocket名称
    public static final String NAME_WEBSOCKET = "environment";
    //WebSocket重连间隔（5秒）
    public static final int WEBSOCKET_RECONNECT_RATE = 5 * 1000;

}
