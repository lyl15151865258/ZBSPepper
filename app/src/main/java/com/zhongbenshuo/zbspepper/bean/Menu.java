package com.zhongbenshuo.zbspepper.bean;

public class Menu {

    // 菜单图标资源ID
    private int menuImg;

    // 菜单文本内容
    private String menuText;

    // 当前是否被选中
    private boolean selected;

    public Menu(int menuImg, String menuText, boolean selected) {
        this.menuImg = menuImg;
        this.menuText = menuText;
        this.selected = selected;
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

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
