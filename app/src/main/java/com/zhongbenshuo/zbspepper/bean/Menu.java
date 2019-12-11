package com.zhongbenshuo.zbspepper.bean;

public class Menu {

    // 当前按钮的ID
    private int id;

    // 菜单图标资源ID
    private int menuImg;

    // 菜单文本内容
    private String menuText;

    // 是否可以点击
    private boolean enable;

    public Menu(int id, int menuImg, String menuText, boolean enable) {
        this.id = id;
        this.menuImg = menuImg;
        this.menuText = menuText;
        this.enable = enable;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getMenuImg() {
        return menuImg;
    }

    public void setMenuImg(int menuImg) {
        this.menuImg = menuImg;
    }

    public String getMenuText() {
        return menuText;
    }

    public void setMenuText(String menuText) {
        this.menuText = menuText;
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }
}
