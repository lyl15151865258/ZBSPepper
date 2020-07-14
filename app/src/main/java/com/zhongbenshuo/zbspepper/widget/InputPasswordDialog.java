package com.zhongbenshuo.zbspepper.widget;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;

import com.zhongbenshuo.zbspepper.R;

/**
 * 管理员输入密码的弹窗
 * Created at 2019/9/5 13:34
 *
 * @author LiYuliang
 * @version 1.0
 */

public class InputPasswordDialog extends Dialog {

    private Context context;
    private EditText etInput;
    private OnDialogClickListener dialogClickListener;

    public InputPasswordDialog(Context context) {
        super(context);
        this.context = context;
        initView();
    }

    //初始化View
    private void initView() {
        setContentView(R.layout.dialog_input_password);
        initWindow();
        etInput = findViewById(R.id.etInput);
        findViewById(R.id.btn_ok).setOnClickListener((v) -> {
            if (dialogClickListener != null) {
                dialogClickListener.onOKClick();
            }
        });
        findViewById(R.id.btn_cancel).setOnClickListener((v) -> {
            dismiss();
            if (dialogClickListener != null) {
                dialogClickListener.onCancelClick();
            }
        });

        // 去掉Android4.4及以下版本出现的顶部横线
        try {
            int dividerID = context.getResources().getIdentifier("android:id/titleDivider", null, null);
            View divider = findViewById(dividerID);
            divider.setBackgroundColor(Color.TRANSPARENT);
        } catch (Exception e) {
            //上面的代码，是用来去除Holo主题的蓝色线条
            e.printStackTrace();
        }
    }

    /**
     * 添加黑色半透明背景
     */
    private void initWindow() {
        Window dialogWindow = getWindow();
        if (dialogWindow != null) {
            dialogWindow.setBackgroundDrawable(new ColorDrawable(0));//设置window背景
            dialogWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);//设置输入法显示模式
            WindowManager.LayoutParams lp = dialogWindow.getAttributes();
            DisplayMetrics d = context.getResources().getDisplayMetrics();
            lp.width = (int) (d.widthPixels * 0.7);
            lp.gravity = Gravity.CENTER;  //中央居中
            dialogWindow.setAttributes(lp);
        }
    }

    public void setOnDialogClickListener(OnDialogClickListener clickListener) {
        dialogClickListener = clickListener;
    }

    /**
     * 添加按钮点击事件
     */
    public interface OnDialogClickListener {
        void onOKClick();

        void onCancelClick();
    }

    public void clearInputContent() {
        etInput.setText("");
    }

    public String getInputContent() {
        return etInput.getText().toString().replace(" ", "");
    }
}

