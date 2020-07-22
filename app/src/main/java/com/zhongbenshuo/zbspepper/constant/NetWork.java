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

    //项目IP地址
    public static final String SERVER_HOST_MAIN = "http://www.zhongbenshuo.com";
    //项目端口号
    public static final String SERVER_PORT_MAIN = "8888";
    //项目名
    public static final String PROJECT_MAIN = "Pepper";

    //资源文件IP地址
    public static final String SERVER_HOST_RESOURCE = "https://www.zhongbenshuo.com";
    //资源文件端口号
    public static final String SERVER_PORT_RESOURCE = "443";

    //http请求超时时间（单位：秒）
    public static final int TIME_OUT_HTTP = 5;

    //WebSocket端口
    public static final String PORT_WEBSOCKET = "9010";
    //WebSocket名称
    public static final String NAME_WEBSOCKET = "environment";
    //WebSocket重连间隔（5秒）
    public static final int WEBSOCKET_RECONNECT_RATE = 5 * 1000;

}
