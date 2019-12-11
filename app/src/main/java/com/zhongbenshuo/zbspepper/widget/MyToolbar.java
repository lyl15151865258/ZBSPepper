package com.zhongbenshuo.zbspepper.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.zhongbenshuo.zbspepper.R;
import com.zhongbenshuo.zbspepper.widget.textview.MarqueeTextView;
import com.zhongbenshuo.zbspepper.widget.textview.MyTextView;

/**
 * 自定义的Toolbar
 * Created at 2019/12/10 0010 9:32
 *
 * @author : LiYuliang
 * @version : 2019/12/10 0010 9:32
 */

public class MyToolbar extends LinearLayout {

    private String titleText;

    private ImageView leftImageView, rightImageView;
    private MarqueeTextView titleTextView;
    private MyTextView leftTextView, rightTextView;
    private FrameLayout toolbarLeft, toolbarRight;

    public MyToolbar(Context context) {
        super(context);
        init(context, null);
    }

    public MyToolbar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        View view = LayoutInflater.from(context).inflate(R.layout.toolbar, this, true);
        leftImageView = view.findViewById(R.id.iv_left);
        rightImageView = view.findViewById(R.id.iv_right);
        titleTextView = view.findViewById(R.id.tv_title);
        leftTextView = view.findViewById(R.id.tv_left);
        rightTextView = view.findViewById(R.id.tv_right);
        toolbarLeft = view.findViewById(R.id.toolbarLeft);
        toolbarRight = view.findViewById(R.id.toolbarRight);
        //删除左右默认的padding
//        setContentInsetsRelative(0, 0);
        // Load attributes
        final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ToolbarControl, 0, 0);
        titleText = a.getString(R.styleable.ToolbarControl_titleText);
        titleTextView.setText(titleText);
        a.recycle();
    }

    /**
     * 设置标题
     *
     * @param titleStr 标题
     */
    public void setTitle(String titleStr) {
        if (titleTextView != null) {
            titleTextView.setText(titleStr);
        }
    }

    public void setTitle(int rid) {
        if (titleTextView != null) {
            titleTextView.setTextById(rid);
        }
    }

    public void setTitle(Object rid) {
        if (titleTextView != null) {
            if (rid instanceof Integer) {
                titleTextView.setTextById((Integer) rid);
            } else if (rid instanceof String) {
                titleTextView.setText((String) rid);
            }
        }
    }

    /**
     * 设置左侧文本
     *
     * @param titleStr 标题
     */
    public void setLeftText(String titleStr) {
        if (TextUtils.isEmpty(titleStr)) {
            leftTextView.setVisibility(GONE);
        } else {
            leftTextView.setText(titleStr);
            leftTextView.setVisibility(VISIBLE);
            leftImageView.setVisibility(GONE);
        }
    }

    public void setLeftText(int rid) {
        if (rid == -1) {
            leftTextView.setVisibility(GONE);
        } else {
            leftTextView.setTextById(rid);
            leftTextView.setVisibility(VISIBLE);
            leftImageView.setVisibility(GONE);
        }
    }

    /**
     * 设置右侧文本
     *
     * @param titleStr 标题
     */
    public void setRightText(String titleStr) {
        if (TextUtils.isEmpty(titleStr)) {
            rightTextView.setVisibility(GONE);
        } else {
            rightTextView.setText(titleStr);
            rightTextView.setVisibility(VISIBLE);
            rightImageView.setVisibility(GONE);
        }
    }

    public void setRightText(int rid) {
        if (rid == -1) {
            rightTextView.setVisibility(GONE);
        } else {
            rightTextView.setTextById(rid);
            rightTextView.setVisibility(VISIBLE);
            rightImageView.setVisibility(GONE);
        }
    }

    public void setLeftButtonImage(int resourceId) {
        if (resourceId == -1) {
            leftImageView.setVisibility(GONE);
        } else {
            leftImageView.setImageResource(resourceId);
            leftImageView.setVisibility(VISIBLE);
            leftTextView.setVisibility(GONE);
        }
    }

    public void setRightButtonImage(int resourceId) {
        if (resourceId == -1) {
            rightImageView.setVisibility(INVISIBLE);
        } else {
            rightImageView.setImageResource(resourceId);
            rightTextView.setVisibility(GONE);
            rightImageView.setVisibility(VISIBLE);
        }
    }

    public void setLeftButtonOnClickListener(OnClickListener listener) {
        if (toolbarLeft != null && listener != null) {
            toolbarLeft.setOnClickListener(listener);
        }
    }

    public void setRightButtonOnClickListener(OnClickListener listener) {
        if (toolbarRight != null && listener != null) {
            toolbarRight.setOnClickListener(listener);
        }
    }

    /**
     * 初始化ToolBar
     *
     * @param title             标题资源文件
     * @param leftImage         左侧图片资源文件
     * @param leftText          左侧文字资源文件
     * @param rightImage        右侧图片资源文件
     * @param rightText         右侧文字资源文件
     * @param onClickListener   点击事件
     */
    public void initToolBar(int title, int leftImage, int leftText, int rightImage, int rightText, OnClickListener onClickListener) {
        // 设置标题
        if (title != -1) {
            setTitle(title);
        }
        // 设置左侧图片或者文字
        if (leftImage != -1) {
            setLeftButtonImage(leftImage);
        } else if (leftText != -1) {
            setLeftText(leftText);
        }
        // 设置右侧图片或者文字
        if (rightImage != -1) {
            setRightButtonImage(rightImage);
        } else if (rightText != -1) {
            setRightText(rightText);
        }
        // 设置监听事件
        setLeftButtonOnClickListener(onClickListener);
        setRightButtonOnClickListener(onClickListener);
    }

    /**
     * 初始化ToolBar
     *
     * @param title             标题文本
     * @param leftImage         左侧图片资源文件
     * @param leftText          左侧文字资源文件
     * @param rightImage        右侧图片资源文件
     * @param rightText         右侧文字资源文件
     * @param onClickListener   点击事件
     */
    public void initToolBar(String title, int leftImage, int leftText, int rightImage, int rightText, OnClickListener onClickListener) {
        // 设置标题
        if (title != null && !TextUtils.isEmpty(title)) {
            setTitle(title);
        }
        // 设置左侧图片或者文字
        if (leftImage != -1) {
            setLeftButtonImage(leftImage);
        } else if (leftText != -1) {
            setLeftText(leftText);
        }
        // 设置右侧图片或者文字
        if (rightImage != -1) {
            setRightButtonImage(rightImage);
        } else if (rightText != -1) {
            setRightText(rightText);
        }
        // 设置监听事件
        setLeftButtonOnClickListener(onClickListener);
        setRightButtonOnClickListener(onClickListener);
    }

}