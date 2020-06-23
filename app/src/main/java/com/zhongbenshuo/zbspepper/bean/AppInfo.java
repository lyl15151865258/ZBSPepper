package com.zhongbenshuo.zbspepper.bean;

import android.graphics.drawable.Drawable;

/**
 * APP信息实体类
 * Created at 2020/6/23 0023 14:15
 *
 * @author : LiYuliang
 * @version : 2020/6/23 0023 14:15
 */

public class AppInfo {

    private String appName;

    private Drawable appIcon;

    private String packageName;

    public AppInfo(String appName, Drawable appIcon, String packageName) {
        this.appName = appName;
        this.appIcon = appIcon;
        this.packageName = packageName;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public Drawable getAppIcon() {
        return appIcon;
    }

    public void setAppIcon(Drawable appIcon) {
        this.appIcon = appIcon;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }
}