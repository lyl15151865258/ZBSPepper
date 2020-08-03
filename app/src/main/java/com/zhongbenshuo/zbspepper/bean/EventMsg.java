package com.zhongbenshuo.zbspepper.bean;

import java.util.Map;

/**
 * Created by LiYuliang on 2017/12/08.
 * 传递消息时使用，可以自己增加更多的参数
 *
 * @author LiYuliang
 * @version 1.1.0
 */

public class EventMsg {

    private String action;
    private String service;
    private String intent;
    private String text;
    private Map<String, String> params;
    private boolean show;

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getIntent() {
        return intent;
    }

    public void setIntent(String intent) {
        this.intent = intent;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Map<String, String> getParams() {
        return params;
    }

    public void setParams(Map<String, String> params) {
        this.params = params;
    }

    public boolean isShow() {
        return show;
    }

    public void setShow(boolean show) {
        this.show = show;
    }
}
