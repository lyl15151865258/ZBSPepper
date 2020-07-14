package com.zhongbenshuo.zbspepper.bean;

import com.zhongbenshuo.zbspepper.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DataBean {

    public Integer imageRes;
    public String imageUrl;
    public String title;
    public int viewType;

    public DataBean(Integer imageRes, String title, int viewType) {
        this.imageRes = imageRes;
        this.title = title;
        this.viewType = viewType;
    }

    public DataBean(String imageUrl, String title, int viewType) {
        this.imageUrl = imageUrl;
        this.title = title;
        this.viewType = viewType;
    }

    public static List<DataBean> getCompanyIntroductionResources() {
        List<DataBean> list = new ArrayList<>();
        list.add(new DataBean(R.drawable.company1, null, 1));
        list.add(new DataBean(R.drawable.company2, null, 1));
        list.add(new DataBean(R.drawable.company3, null, 1));
        list.add(new DataBean(R.drawable.company4, null, 1));
        list.add(new DataBean(R.drawable.company5, null, 1));
        list.add(new DataBean(R.drawable.company6, null, 1));
        list.add(new DataBean(R.drawable.company7, null, 1));
        list.add(new DataBean(R.drawable.company8, null, 1));
        list.add(new DataBean(R.drawable.company9, null, 1));
        list.add(new DataBean(R.drawable.company10, null, 1));
        list.add(new DataBean(R.drawable.company11, null, 1));
        list.add(new DataBean(R.drawable.company12, null, 1));
        list.add(new DataBean(R.drawable.company13, null, 1));
        list.add(new DataBean(R.drawable.company14, null, 1));
        return list;
    }

    public static List<DataBean> getBusinessScopeResources() {
        List<DataBean> list = new ArrayList<>();
        list.add(new DataBean(R.drawable.business1, null, 1));
        list.add(new DataBean(R.drawable.business2, null, 1));
        return list;
    }

    public static List<DataBean> getEngineeringCaseResources() {
        List<DataBean> list = new ArrayList<>();
        list.add(new DataBean(R.drawable.project1, null, 1));
        list.add(new DataBean(R.drawable.project2, null, 1));
        list.add(new DataBean(R.drawable.project3, null, 1));
        list.add(new DataBean(R.drawable.project4, null, 1));
        list.add(new DataBean(R.drawable.project5, null, 1));
        list.add(new DataBean(R.drawable.project6, null, 1));
        list.add(new DataBean(R.drawable.project7, null, 1));
        list.add(new DataBean(R.drawable.project8, null, 1));
        list.add(new DataBean(R.drawable.project9, null, 1));
        list.add(new DataBean(R.drawable.project10, null, 1));
        list.add(new DataBean(R.drawable.project11, null, 1));
        list.add(new DataBean(R.drawable.project12, null, 1));
        list.add(new DataBean(R.drawable.project13, null, 1));
        list.add(new DataBean(R.drawable.project14, null, 1));
        list.add(new DataBean(R.drawable.project15, null, 1));
        list.add(new DataBean(R.drawable.project16, null, 1));
        return list;
    }

    public static List<DataBean> getAskContent() {
        List<DataBean> list = new ArrayList<>();
        list.add(new DataBean(0, "今天天气怎么样？", 1));
        list.add(new DataBean(0, "5加3等于几?", 1));
        list.add(new DataBean(0, "讲一个笑话吧", 1));
        list.add(new DataBean(0, "打开音乐播放器", 1));
        list.add(new DataBean(0, "丁忆在办公室吗？", 1));
        return list;
    }

    public static List<String> getColors(int size) {
        List<String> list = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            list.add(getRandColor());
        }
        return list;
    }

    /**
     * 获取十六进制的颜色代码.例如  "#5A6677"
     * 分别取R、G、B的随机值，然后加起来即可
     *
     * @return String
     */
    public static String getRandColor() {
        String R, G, B;
        Random random = new Random();
        R = Integer.toHexString(random.nextInt(256)).toUpperCase();
        G = Integer.toHexString(random.nextInt(256)).toUpperCase();
        B = Integer.toHexString(random.nextInt(256)).toUpperCase();

        R = R.length() == 1 ? "0" + R : R;
        G = G.length() == 1 ? "0" + G : G;
        B = B.length() == 1 ? "0" + B : B;

        return "#" + R + G + B;
    }
}
