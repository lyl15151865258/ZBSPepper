package com.zhongbenshuo.zbspepper.design.activity.utils;

public class ScreenFlagsChecker {

    public boolean hasFlags(int visibility, int... flags) {
        for (int flag : flags) {
            if ((visibility & flag) != flag) {
                return false;
            }
        }
        return true;
    }
}